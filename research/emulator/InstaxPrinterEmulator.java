import java.util.concurrent.atomic.*;
import java.lang.Thread;
import java.io.*;
import java.net.*;
import java.util.Scanner;
import java.util.Arrays;

public class InstaxPrinterEmulator extends Thread
{
	private static final int PORT = 8080;
	private AtomicBoolean exit;
	private AtomicBoolean coverOpen;
	private AtomicBoolean filmEmpty;
	private AtomicInteger filmLeft;
	private AtomicInteger battState;
	
	private ServerSocket servSock;
	private Socket client;
	
	private int sequence = 0;
	private int progress = 11;
	BufferedOutputStream imageFile;
	
	
	public InstaxPrinterEmulator()
	{
		exit = new AtomicBoolean(false);
		coverOpen = new AtomicBoolean(false);
		filmEmpty = new AtomicBoolean(false);
		filmLeft = new AtomicInteger(10);
		battState = new AtomicInteger(4);
		imageFile = null;
	}
	
	public InstaxFrame recvPacket() throws IOException, InvalidFrameException, InterruptedException, ClientNotConnectedException
	{
		InstaxFrame incommingFrame = new InstaxFrame();
		InputStream stream = client.getInputStream(); 
		boolean packetReceived = false;
		int read;
		while(!packetReceived && !exit.get())
		{
			try
			{
				//System.out.println("Reading");
				read = stream.read();
				if(read < 0)
					throw new ClientNotConnectedException("Connection closed");
				//System.out.println("Read: " + read);
				packetReceived = incommingFrame.recvPacket(read);
			}
			catch(SocketTimeoutException e)
			{
				continue;
				//System.out.println("Socket Timed Out");
			}
			//System.out.println("Trying again.");
		}
		System.out.println("PacketReceived. SID: " + Integer.toHexString(incommingFrame.getSID()));
		return !exit.get() ? incommingFrame : null; //If we have to exit, then return null
	}
	
	public void sendPacket(int[] responsePacket) throws IOException
	{
		BufferedOutputStream output = new BufferedOutputStream(client.getOutputStream());
		for(int i = 0; i < responsePacket.length; i++)
			output.write(responsePacket[i]);
		output.flush();
	}
	
	public int[] processPacket(InstaxFrame frame)
	{
		int SID = frame.getSID();
		System.out.println("SID Received: " + InstaxProtocolConsts.SidToString(SID));
		int[] data;
		switch(SID)
		{
			case InstaxProtocolConsts.SID_HELLO:
				data = getHello();
				break;
			case InstaxProtocolConsts.SID_STATUS:
				data = getStatus();
				break;
			case InstaxProtocolConsts.SID_SHADING_DATA:
				data = provideShadingData(frame);
				break;
			case InstaxProtocolConsts.SID_FUNCTION_VERSION:
				data = provideFunctionVersion(frame);
				break;
			case InstaxProtocolConsts.SID_GET_INSTAXPARAMETER:
				data = provideInstaxParameter();
				break;
			case InstaxProtocolConsts.SID_LOCK_INSTAX:
			case InstaxProtocolConsts.SID_PREP_PRINTER:
				data = new int[4];
				Arrays.fill(data, 0x00);
				break;
			case InstaxProtocolConsts.SID_TRANSFER_START:
				data = transferImageStart();
				break;
			case InstaxProtocolConsts.SID_SEND_IMAGE:
				data = recieveImage(frame);
				break;
			case InstaxProtocolConsts.SID_TRANSFER_END:
				data = transferImgEnd();
				break;
			case InstaxProtocolConsts.SID_START_PRINT:
				data = startPrint();
				break;
			case InstaxProtocolConsts.SID_CHECK_PROGRESS:
				data = checkProgress();
				break;
			default:
				return null;
		}
		if(data == null)
		{
			System.out.println("Null Data");
			return null;
		}
		//Inject status byte
		if(data.length >= 3)
			data[3] = buildByte15();
		
		return InstaxFrame.buildOutgoingPacket(InstaxFrame.PRINTER_FRAME_START, frame.getSID(), frame.getUID(), InstaxFrame.PRINTER_DEFAULT_PASSWORD, data);
	}
	
	public int[] getHello()
	{
		System.out.println("Responding with hello");
		//The response code should be set to 0
		//I don't think any other data is required.
		//The byte index 2 value may be required since the device looks for it.
		//However, they appear to have good bounds checking and may not have problems.
		//int[] helloResp = { 0x00, 0x00, 0x00, 0x00, 0x48, 0x03, 0x01, 0x03, 0x22, 0x02, 0x13, 0x00, 0x00};
		int[] helloResp = { InstaxProtocolConsts.RSP_RET_OK, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};
		return helloResp;
	}
	
	public int[] getStatus()
	{
		System.out.println("Responding with status");
		// Response code 0.
		int[] status = { InstaxProtocolConsts.RSP_RET_OK, 0x00, 0x00, 0x1A, 0x53, 0x50, 0x2D, 0x31};
		
		return status;
	}
	
	public int[] provideShadingData(InstaxFrame frame)
	{
		int magicNumber1 = 0x4A;
		int length = 484;
		int[] shadingData = new int[length];
		Arrays.fill(shadingData, 0);
		shadingData[7] = frame.getDataByte(3);
		return shadingData;
	}
	
	public int[] provideFunctionVersion(InstaxFrame frame)
	{
		int functionId = frame.getInt(0);
		System.out.println("Function id: " + functionId);
		int[] data = { InstaxProtocolConsts.RSP_RET_OK, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 };
		data[5] = functionId;
		switch(functionId)
		{
			case 1:
			case 2:
			case 3:
			case 4:
				data[7] = 1;
				break;
			case 5:
			case 6:
			case 7:
			case 8:
			case 9:
			case 10:
				data[7] = 0;
				break;
			default:
				return null;
		}
		return data;
	}
	
	public int[] provideInstaxParameter()
	{
		System.out.println("InstaxParameter providing");
		//int[] data = new int[12];
		//Arrays.fill(data, 0x00);
		int[] data = { InstaxProtocolConsts.RSP_RET_OK, 0x00, 0x00, 0x4A, 0x00, 0x00, 0x00, 0x48, 0x01, 0xC7, 0x00, 0x00, 0x03, 0xFE, 0x00, 0x0D, 0x19, 0x01, 0x00, 0x00};
		//System.out.println("Finished");
		return data;
	}
	
	public int[] transferImageStart()
	{
		int[] data = { InstaxProtocolConsts.RSP_RET_OK, 0x00, 0x00, 0x00, 0x00, 0x00, 0x03, 0xC0};
		try
		{
			if(imageFile != null)
				imageFile.close();
			imageFile = null;
			imageFile = new BufferedOutputStream(new FileOutputStream("img_" + System.currentTimeMillis() + ".jpg"));
			sequence = 0;
		}
		catch(IOException e)
		{
			System.out.println("Error opening file");
			imageFile = null;
		}
		return data;
	}
	
	public int[] recieveImage(InstaxFrame frame)
	{
		int[] rData = frame.getDataArray();
		System.out.println("Data Array Size: " + rData.length);
		if(imageFile == null)
			return null;
		try
		{
			int recvSequence = frame.getInt(0);
			if(recvSequence != sequence)
			{
				System.out.println("Out of sequence packet");
				return null; //phone will retry
			}
			for(int i = 4; i < rData.length; i++)
			{
				imageFile.write(rData[i] & 0xFF);
			}
			imageFile.flush();
		}
		catch(IOException e)
		{
			System.out.println(e.getMessage());
		}
		
		int[] data = { InstaxProtocolConsts.RSP_RET_OK, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 };
		data[7] = sequence++;
		return data;
	}
	
	public int[] transferImgEnd()
	{
		try
		{
			if(imageFile != null)
				imageFile.close();
		}
		catch(IOException e)
		{
			System.out.println("Error closing file");
		}
		int[] data = {InstaxProtocolConsts.RSP_RET_OK, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 };
		return data;
		
	}
	
	public int[] startPrint()
	{
		int[] data = { InstaxProtocolConsts.RSP_RET_OK, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};
		progress = 10;
		int magicNumber = 100;
		data[7] = 0xFF & magicNumber;
		return data;
	}
	
	public int[] checkProgress()
	{
		int[] data = new int[4];
		Arrays.fill(data, 0x00);
		if(progress > 0)
		{
			data[0] = InstaxProtocolConsts.RSP_E_PRINTING;
			progress--;
		}
		else
		{
			data[0] = InstaxProtocolConsts.RSP_RET_OK;
		}
		return data;
	}
	
	private int buildByte15()
	{
		int b = 0x00;
		
		//System.out.println("Film Left: " + filmLeft.get());
		b |= filmLeft.get();

		b |= battState.get() << 4; //Battery Value. 4 = plugged in.
		
		if(coverOpen.get())
			b |= 0x80;
		
		//System.out.println("B Value: " + b);
		return b;
	}
	
	public void run()
	{
		runServer();
	}
	
	public void runServer()
	{
		try
		{
			servSock = new ServerSocket(PORT);
			System.out.println("Server started:");
		}catch(IOException e)
		{
			System.out.println("Error opening serverSocket.");
			exit.set(true);
			return;
		}
		while(!exit.get())
		{
			System.out.println("Waiting for connection.");
			try
			{
				//There are several steps to this. 
				// Accept a client
				client = servSock.accept();
				client.setSoTimeout(1000);
				System.out.println("Client Connected.");
				while(!exit.get())
				{
					//Then listen for a frame
					//Process the frame
					//Send a response.
					InstaxFrame recvFrame = recvPacket();
					if(recvFrame == null)
						continue; //Generally the exit condition
					int[] responsePacket = processPacket(recvFrame);
					if(responsePacket == null)
					{
						System.out.println("No Response");
						continue;
					}
					System.out.println("Sending Response");
					sendPacket(responsePacket);
				}
			}
			catch(InvalidFrameException e)
			{
				System.out.println("Client sent invalid frame");
				System.out.println(e.getMessage());
				continue; //This is not necessarily a fatal exception
			}
			catch(InterruptedException e)
			{
				System.out.println("Timeout interrupted.");
				System.out.println(e.getMessage());
				continue; //I don't think this will be a fatal exception
			}
			catch(ClientNotConnectedException e)
			{
				System.out.println(e.getMessage());
				continue;
				
			}
			catch(IOException e)
			{
				System.out.println("Error communicating with socket.");
				exit.set(true);
				break; //This is a fatal exception since the socket is not good anymore.
			}
		}
		try
		{
			if(client != null)
				client.close();
			servSock.close();
		}catch(IOException e)
		{
			System.out.println("Error closing sockets.");
			System.out.println(e.getMessage());
		}
		finally
		{
			client = null;
			servSock = null;
		}
	}
	
	public static void printMenu()
	{
		System.out.println("InstaxPrinterEmulator.");
		System.out.println("1. Start Server");
		System.out.println("2. Toggle Cover");
		System.out.println("3. Toggle Film Status");
		System.out.println("4. Set Film Count");
		System.out.println("5. Change battery state");
		System.out.println("6. Exit");
		System.out.print("Please Make a Selection: ");
	}
	
	public static void main(String[] args) throws InterruptedException, IOException
	{
		InstaxPrinterEmulator emulator = new InstaxPrinterEmulator();
		Scanner scanner = new Scanner(System.in);
		while(!emulator.exit.get())
		{
			printMenu();
			int selection = scanner.nextInt();
			switch(selection)
			{
				case 1:
					emulator.start();
					break;
				case 2:
					emulator.coverOpen.set(!emulator.coverOpen.get());
					System.out.println("coverOpen: " + emulator.coverOpen.get());
					break;
				case 3:
					emulator.filmEmpty.set(!emulator.filmEmpty.get());
					System.out.println("filmEmpty: " + emulator.filmEmpty.get());
					break;
				case 4:
					System.out.print("Enter Film Count: ");
					emulator.filmLeft.set(scanner.nextInt());
					break;
				case 5:
					System.out.print("Enter Batt State Value: ");
					emulator.battState.set(scanner.nextInt());
					break;
				case 6:
					emulator.exit.set(true);
					if(emulator.servSock != null)
						emulator.servSock.close();
					emulator.join();
					break;
				default:
					System.out.println("Invalid input. Please try again");
			}
		}
	}
	
}