public class InstaxProtocolConsts
{
	public static final int SID_HELLO = 0xC0;
	public static final int SID_STATUS = 0xC2;
	public static final int SID_SHADING_DATA = 0x40;
	public static final int SID_FUNCTION_VERSION = 0xC4;
	public static final int SID_GET_INSTAXPARAMETER = 0xC1;
	public static final int SID_LOCK_INSTAX = 0xB3;
	public static final int SID_PREP_PRINTER = 0x50;
	public static final int SID_TRANSFER_START = 0x51;
	public static final int SID_SEND_IMAGE = 0x52;
	public static final int SID_TRANSFER_END = 0x53;
	public static final int SID_START_PRINT = 0xB0;
	public static final int SID_CHECK_PROGRESS = 0xC3;
	
	public static final int RSP_RET_OK = 0x00;
	public static final int RSP_RET_HOLD = 0x7F;
	public static final int RSP_E_PRINTING = 0xA3;
	public static final int RSP_E_EJECTING = 0xA4;
	
	
	public static String SidToString(int sid)
	{
		switch(sid)
		{
			case SID_HELLO:
				return "SID_HELLO";
			case SID_STATUS:
				return "SID_STATUS";
			case SID_SHADING_DATA:
				return "SID_SHADING_DATA";
			case SID_FUNCTION_VERSION:
				return "SID_FUNCTION_VERSION";
			case SID_GET_INSTAXPARAMETER:
				return "SID_GET_INSTAXPARAMETER";
			case SID_LOCK_INSTAX:
				return "SID_LOCK_INSTAX";
			case SID_PREP_PRINTER:
				return "SID_PREP_PRINTER";
			case SID_TRANSFER_START:
				return "SID_TRANSFER_START";
			case SID_SEND_IMAGE:
				return "SID_SEND_IMAGE";
			case SID_TRANSFER_END:
				return "SID_TRANSFER_END";
			case SID_START_PRINT:
				return "SID_START_PRINT";
			case SID_CHECK_PROGRESS:
				return "SID_CHECK_PROGRESS";
			default:
				return "Unknown SID: " + sid;
		}
	}
}