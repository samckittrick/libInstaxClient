public class test
{
	public static void main(String[] args)
	{
		int i = 0x4A;
		int n = (0x7 & (i >> 4));
		System.out.println("Number: " + n);
	}
}
