import java.io.*;
import java.net.*;
import java.util.Scanner;

public class InstaxClientEmulator
{
	public static final String ip = "192.168.0.251";
	public static final int port = 8080;
	private Socket sock;
	private int UID;
	private short password = (short)0xFFFF;
	private BufferedOutputStream out;
	private InputStream in;
	
	public static void main(String[] args)
	{
		InstaxClientEmulator emulator = new InstaxClientEmulator();
		boolean exit = false;
		Scanner scanner = new Scanner(System.in);
		while(!exit)
		{
			printMenu();
			int input = scanner.nextInt();
			switch(input)
			{
				case 1:
					emulator.connect();
					break;
				case 2:
					emulator.sendHello();
					break;
				case 3:
					emulator.sendStatus();
					break;
				case 4:
					emulator.getShadingData();
					break;
				case 5:
					System.out.print("Enter a function Id:");
					int value = scanner.nextInt();
					emulator.getFunctionVersion(value);
					break;
				case 6:
					emulator.getInstaxParameter();
					break;
				case 7:
					emulator.lockPrinter(true);
					break;
				case 8:
					emulator.lockPrinter(false);
					break;
				case 9:
					emulator.prepPrinter();
					break;
				case 10:
					emulator.transferStart();
					break;
				case 11:
					System.out.print("Enter a new Password: ");
					short p = scanner.nextShort();
					emulator.setPassword(p);
					break;
				case 12:
					emulator.disconnect();
					exit = true;;
					break;
				default:
					System.out.println("Invalid input. Please try again");
			}
		}
		scanner.close();
		
	}
	
	private static void printMenu()
	{
		System.out.println("InstaxClientEmulator.");
		System.out.println("1. Connect");
		System.out.println("2. Hello");
		System.out.println("3. Status");
		System.out.println("4. Get Shading Data");
		System.out.println("5. Get function Data");
		System.out.println("6. Get Instax Parameter");
		System.out.println("7. Lock Printer");
		System.out.println("8. Unlock Printer");
		System.out.println("9. Prep Printer");
		System.out.println("10. Signal transfer start");
		System.out.println("11. Change Password Locally");
		System.out.println("12. Exit");
		System.out.print("Please Make a Selection: ");
	}
	
	private void setPassword(short p)
	{
		password = p;
	}
	
	public InstaxClientEmulator()
	{
		UID = 0x01020304;
		out = null;
		in = null;
	}
	
	public void connect()
	{
		if(sock != null && sock.isConnected())
		{
			System.out.println("Already connected");
			return;
		}
		try
		{
			sock = new Socket(ip, port);
			out = new BufferedOutputStream(sock.getOutputStream());
			in = sock.getInputStream();
		}
		catch(UnknownHostException e)
		{
			System.out.println(e.getMessage());
		}
		catch(IOException e)
		{
			e.getMessage();
		}
	}
	
	public void disconnect()
	{
		if(sock == null)
		{
			System.out.println("Already disconnected");
			return;
		}
		try{
			out.close();
			out = null;
			in.close();
			in = null;
			sock.close();
		}
		catch(IOException e)
		{
			System.out.println(e.getMessage());
		}
		finally
		{
			sock = null;
		}
		
	}
	
	public void sendHello()
	{
		try
		{
			InstaxFrame response = sendData(InstaxProtocolConsts.SID_HELLO, null);
			System.out.print("Response " + response.toString());
		}catch(ClientNotConnectedException e)
		{
			System.out.println(e.getMessage());
		}
		catch(InvalidFrameException e)
		{
			System.out.println(e.getMessage());
		}
	}
	
	public void sendStatus()
	{
		try
		{
			InstaxFrame response = sendData(InstaxProtocolConsts.SID_STATUS, null);
			System.out.print("Response " + response.toString());
		}catch(ClientNotConnectedException e)
		{
			System.out.println(e.getMessage());
		}
		catch(InvalidFrameException e)
		{
			System.out.println(e.getMessage());
		}
	}
	
	public void getInstaxParameter()
	{
		try
		{
			InstaxFrame response = sendData(InstaxProtocolConsts.SID_GET_INSTAXPARAMETER, null);
			System.out.print("Response " + response.toString());
		}catch(ClientNotConnectedException e)
		{
			System.out.println(e.getMessage());
		}
		catch(InvalidFrameException e)
		{
			System.out.println(e.getMessage());
		}
	}
	
	public void getShadingData()
	{
		int[] data = { 0x00, 0x00, 0x00, 0x00 };
		for(int i = 0; i < 3; i++)
		{
			try
			{
				data[3] = 0xFF & i;
				InstaxFrame response = sendData(InstaxProtocolConsts.SID_SHADING_DATA, data);
				System.out.println("Response " + response.toString());
			}catch(ClientNotConnectedException e)
			{
				System.out.println(e.getMessage());
			}
			catch(InvalidFrameException e)
			{
				System.out.println(e.getMessage());
			}
		}
	}
	
	public void getFunctionVersion(int value)
	{
		int[] data = { 0x00, 0x00, 0x00, 0x00 };
		data[3] = value;
		try
		{
			InstaxFrame response = sendData(InstaxProtocolConsts.SID_FUNCTION_VERSION, data);
			System.out.println("Response " + response.toString());
		}catch(ClientNotConnectedException e)
		{
			System.out.println(e.getMessage());
		}
		catch(InvalidFrameException e)
		{
			System.out.println(e.getMessage());
		}
	}
	
	public void lockPrinter(boolean locked)
	{
		int[] data = { 0x00, 0x00, 0x00, 0x00};
		data[0] = locked ? 0x01 : 0x00;
		
		try
		{
			InstaxFrame response = sendData(InstaxProtocolConsts.SID_LOCK_INSTAX, data);
			System.out.print("Response " + response.toString());
		}catch(ClientNotConnectedException e)
		{
			System.out.println(e.getMessage());
		}
		catch(InvalidFrameException e)
		{
			System.out.println(e.getMessage());
		}
	}
	
	public void prepPrinter()
	{
		try
		{
			InstaxFrame response = sendData(InstaxProtocolConsts.SID_PREP_PRINTER, null);
			System.out.print("Response " + response.toString());
		}catch(ClientNotConnectedException e)
		{
			System.out.println(e.getMessage());
		}
		catch(InvalidFrameException e)
		{
			System.out.println(e.getMessage());
		}
	}
	
	public void transferStart()
	{
		int[] data = { 0x02, 0x00, 0x00, 0x01, 0x37, 0xE7, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00}; //Hardcoded for now. 
		try
		{
			InstaxFrame response = sendData(InstaxProtocolConsts.SID_TRANSFER_START, data);
			System.out.print("Response " + response.toString());
		}catch(ClientNotConnectedException e)
		{
			System.out.println(e.getMessage());
		}
		catch(InvalidFrameException e)
		{
			System.out.println(e.getMessage());
		}
	}
	
	public InstaxFrame sendData(int SID, int[] data) throws ClientNotConnectedException, InvalidFrameException
	{
		if(sock == null | !sock.isConnected())
		{
			throw new ClientNotConnectedException("Client not connected to printer");
		}
		int[] outgoingFrame = InstaxFrame.buildOutgoingPacket(InstaxFrame.CLIENT_FRAME_START, SID, UID, password, data);

		try
		{
			for(int i = 0; i < outgoingFrame.length; i++)
				out.write(outgoingFrame[i]);
			out.flush();
			InstaxFrame responseFrame = new InstaxFrame();
			boolean packetReceived = false;
			while(!packetReceived)
				packetReceived = responseFrame.recvPacket(in.read());
			return responseFrame;
		}
		catch(IOException e)
		{
			throw new InvalidFrameException("Error reading/writing from/to descriptors: " + e.getMessage());
		}
	}
	
	public void setNewPassword(short password)
	{
		this.password = password;
	}

}