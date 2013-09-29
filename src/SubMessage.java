
public class SubMessage {
	private String title;
	private boolean isSigned;
	private boolean isBigEndian;
	private double scalar;
	//private String units;

	public SubMessage(String title, boolean isSigned, boolean isBigEndian, float scalar, String units) {
		this.title = title;
		this.isSigned = isSigned;
		this.isBigEndian = isBigEndian;
		this.scalar = scalar;
		//this.units = units;
	}
	
	public String translateData(byte one, byte two) {
		String printString = "";
		if(title.equals("Unused") || title.equals("Reserved")) return printString;
		
		String hexString = isBigEndian ? hex(one) + hex(two) : hex(two) + hex(one);
		int hexValue = Integer.parseInt(hexString,16);
		
		if(title.equals("Yaw Rate")) hexValue -= 0x8000;
		if(title.equals("Yaw Acceleration")) hexValue -= 0x8000;
		if(title.equals("Lateral Acceleration")) hexValue -= 0x8000;
		if(title.equals("Longitudinal Acceleration")) hexValue -= 0x8000;
		if(title.equals("Brake Pressure 0")) hexValue -= 409.6;
		if(title.equals("Brake Pressure 1")) hexValue -= 409.6;
		
		if(isSigned) {
			printString = title + "," + round((((short)hexValue) * scalar)) + "\n";
		}
		else {
			printString = title + "," + round((hexValue * scalar)) + "\n";
		}
		return printString;
	}
	
	public String hex(byte num) {
		return String.format("%02x", num);
	}
	
	public double round(double num) {
		final int SIG_FIGS = 6;
	    if(num == 0) {
	        return 0;
	    }

	    final double d = Math.ceil(Math.log10(num < 0 ? -num: num));
	    final int power = SIG_FIGS - (int) d;

	    final double magnitude = Math.pow(10, power);
	    final long shifted = Math.round(num*magnitude);
	    return shifted/magnitude;
	}
}