import java.util.ArrayList;

public class InstaxFrame
{
	//Packet Definitions
	protected static final int PACKET_SID_OFFSET = 1;
	protected static final int PACKET_LENGTH_OFFSET = 2;
	protected static final int PACKET_UID_OFFSET = 4;
	protected static final int PACKET_PASSWORD_OFFSET = 8;
	protected static final int PACKET_DATA_OFFSET = 12;
	protected static final int PACKET_HEADER_LENGTH = 12;
	protected static final int PACKET_CHECKSUM_LENGTH = 4;
	protected static final int magicNumber1 = 13;
	protected static final int magicNumber2 = 10;
	
	public static final int CLIENT_FRAME_START = 0x24;
	public static final int PRINTER_FRAME_START = 0x2A;
	public static final short CLIENT_DEFAULT_PASSWORD = (short)0xFFFF;
	public static final short PRINTER_DEFAULT_PASSWORD = (short)0x0000;
	
	protected static final int startingArrLength = 20;
	
	//Packet Arrays
	protected ArrayList<Integer> incommingArray;
	
	//Frame header data
	protected int SID; //Command id
	protected int packetLength; //Length of whole packet including header
	protected int UID; //Device id
	protected short password;
	
	public InstaxFrame()
	{
		incommingArray = new ArrayList<Integer>(startingArrLength);
	}
	
	public static int[] buildOutgoingPacket(int startByte, int SID, int UID, short password, int[] dataArr)
	{
		int[] data;
		if(dataArr == null)
			data = new int[0];
		else
			data = dataArr;
		
		int packetLength = data.length + PACKET_CHECKSUM_LENGTH + PACKET_HEADER_LENGTH;
		int[] outgoingArray = new int[packetLength];
		int pointer = 0;
		//Packet start value
		outgoingArray[pointer++] = startByte;
		//Command id
		outgoingArray[pointer++] = SID;
		//Packet length
		outgoingArray[pointer++] = 0xFF & (packetLength >> 8);
		outgoingArray[pointer++] = 0xFF & (packetLength);
		//device id
		outgoingArray[pointer++] = 0xFF & (UID >> 24);
		outgoingArray[pointer++] = 0xFF & (UID >> 16);
		outgoingArray[pointer++] = 0xFF & (UID >> 8);
		outgoingArray[pointer++] = 0xFF & (UID);
		//Password
		outgoingArray[pointer++] = 0xFF & (password >> 8);
		outgoingArray[pointer++] = 0xFF & (password);
		//Header end bytes
		outgoingArray[pointer++] = 0x00;
		outgoingArray[pointer++] = 0x00;
		
		//Write the data
		System.arraycopy(data, 0, outgoingArray, pointer, data.length);
		pointer += data.length;
		
		//Calculate the checksum
		int[] cksum = calculateChecksum(outgoingArray, packetLength - 4);
		outgoingArray[pointer++] = cksum[0];
		outgoingArray[pointer++] = cksum[1];
		outgoingArray[pointer++] = cksum[2];
		outgoingArray[pointer++] = cksum[3];
		return outgoingArray;
	}
	
	//We will read the packet one byte at a time for simplicity on an untrusted byte stream.
	public boolean recvPacket(int inByte) throws InvalidFrameException
	{
		incommingArray.add(inByte);
		//If we have the header, parse it.
		if(incommingArray.size() == PACKET_HEADER_LENGTH)
			parseHeader();
		
		//If we haven't received the full header yet, we haven't gotten the full packet.
		if(incommingArray.size() < PACKET_HEADER_LENGTH)
			return false;
		
		//If we have received the entire packet
		if(incommingArray.size() == packetLength)
		{
			if(verifyChecksum(incommingArray.toArray(new Integer[0]), incommingArray.size() - 4))
				return true;
			else
				throw new InvalidFrameException("Checksum verification failed");
		}
		
		//If we have made it this far, we haven't gotten the packet yet.
		return false;
	}
	
	protected void parseHeader() throws InvalidFrameException
	{
		//System.out.println("Header received.");
		if(incommingArray.size() < PACKET_HEADER_LENGTH)
			throw new InvalidFrameException("Packet is shorter than header length");
			
		SID = incommingArray.get(PACKET_SID_OFFSET);
		packetLength = (0xFF & incommingArray.get(PACKET_LENGTH_OFFSET)) << 8  | (0xFF & (int)incommingArray.get(PACKET_LENGTH_OFFSET + 1));
		//System.out.println("Packet Length: " + packetLength);
		UID = (int)(0xFF & incommingArray.get(PACKET_UID_OFFSET)) << 24 |
			  (int)(0xFF & incommingArray.get(PACKET_UID_OFFSET + 1)) << 16 |
			  (int)(0xFF & incommingArray.get(PACKET_UID_OFFSET + 2)) << 8 |
			  (int)(0xFF & incommingArray.get(PACKET_UID_OFFSET + 3));
		password = (short)((0xFF & incommingArray.get(PACKET_PASSWORD_OFFSET)) << 8  | (0xFF & (int)incommingArray.get(PACKET_PASSWORD_OFFSET + 1)));
	}
	
	protected static int[] calculateChecksum(final int[] array, final int n)
	{
		int i = 0;
		int cksum = 0;
		int[] cksumBytes = new int[4];
		while(i < n)
		{
			cksum += (0xFF & array[i]);
			i++;
		}
		cksumBytes[0] = (int)(0xFF & ~cksum >> 8);
		cksumBytes[1] = (int)(0xFF & ~cksum >> 0);
		cksumBytes[2] = 13;
		cksumBytes[3] = 10;
		return cksumBytes;
	}
	
	protected static boolean verifyChecksum(final Integer[] array, int n)
	{
		int cksum = 0;
		for(int i = 0; i < n; i++)
		{
			cksum += (0xFF & array[i]);
		}
		//System.out.println("Calculated: " + Integer.toHexString(cksum));
		return (0xFFFF & cksum + ((0xFF & array[n]) << 8 | (0xFF & array[n+1])<<0)) == 0xFFFF && array[n+2] == magicNumber1 && array[n+3] == magicNumber2;
	}
	
	public int getSID()
	{
		return SID;
	}
	
	public int getUID()
	{
		return UID;
	}
	
	public short getPassword()
	{
		return password;
	}
	
	public String toString()
	{
		StringBuilder b = new StringBuilder();
		b.append("InstaxFrame Dump: \n");
		b.append("SID: " + Integer.toHexString(SID) + "\n");
		b.append("UID: " + Integer.toHexString(UID) + "\n");
		b.append("Data: " );
		for(int i = 0; i < incommingArray.size(); i++)
		{
			b.append(Integer.toHexString(incommingArray.get(i)) + " ");
		}
		b.append("\n");
		return b.toString();
	}
	
	public int getDataByte(int n)
	{
		return incommingArray.get(PACKET_HEADER_LENGTH + n);
	}

	public int getShort(int n)
	{
		return (incommingArray.get(PACKET_HEADER_LENGTH + n) & 0xFF)<<8 | (incommingArray.get(PACKET_HEADER_LENGTH + n + 1)&0xFF);
	}
	
	public int getInt(int n)
	{
		return (incommingArray.get(PACKET_HEADER_LENGTH + n) & 0xFF << 24) | (incommingArray.get(PACKET_HEADER_LENGTH + n + 1) & 0xFF << 16) | (incommingArray.get(PACKET_HEADER_LENGTH + n + 2) & 0xFF << 8) | (incommingArray.get(PACKET_HEADER_LENGTH + n + 3) & 0xFF << 0);
	}
	
	public int[] getDataArray()
	{
		int dataLength = packetLength - PACKET_HEADER_LENGTH - PACKET_CHECKSUM_LENGTH;
		System.out.println("PacketLength: " + packetLength);
		System.out.println("DataLength: " + dataLength);
		int[] data = new int[dataLength];
		for(int i = 0; i < dataLength; i++)
			data[i] = incommingArray.get(PACKET_HEADER_LENGTH + i);
		return data;
	}
}