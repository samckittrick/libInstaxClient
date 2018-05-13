public class InstaxFrameTest
{
	public static void main(String[] args) throws InvalidFrameException
	{
		InstaxFrame Frame = new InstaxFrame();
		int[] incommingClientPacket = { 0x24, 0xC0, 0x00, 0x10, 0xDC, 0x5D, 0x89, 0x5B, 0xFF, 0xFF, 0x00,0x00, 0xFA, 0xF0, 0x0D, 0x0A };
		
		System.out.println("Packet incoming test");
		int i = 0;
		boolean packetReceived = false;
		while(!packetReceived)
		{
			packetReceived = Frame.recvPacket(incommingClientPacket[i]);
			i++;
		}
		
		System.out.println("SID: " + Integer.toHexString(Frame.getSID()));
		int SID = Frame.getSID();
		System.out.println("UID: " + Integer.toHexString(Frame.getUID()));
		int UID = Frame.getUID();
		
		int[] outboundPacket = Frame.buildOutgoingPacket(InstaxFrame.CLIENT_FRAME_START, SID, UID, (short)0xFFFF, null );
		
		System.out.println("Outbound packet");
		for(int j = 0; j < outboundPacket.length; j++)
		{
			System.out.print(Integer.toHexString(outboundPacket[j]) + " " );
		}
		System.out.println("");
	}

}