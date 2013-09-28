public class SubMessage {
	private String title;
	private boolean isSigned;
	private boolean isBigEndian;
	private double scalar;
	private String units;

	public SubMessage(String title, boolean isSigned, boolean isBigEndian, float scalar, String units) {
		this.title = title;
		this.isSigned = isSigned;
		this.isBigEndian = isBigEndian;
		this.scalar = scalar;
		this.units = units;
	}
	
	public String translateData(byte one, byte two) {
		String printString = "";
		if(title.equals("Unused") || title.equals("Reserved")) return printString;
		
		String hexString = isBigEndian ? hex(one) + hex(two) : hex(two) + hex(one);
		int hexValue = Integer.parseInt(hexString,16);
		
		if(title.equals("Yaw Rate")) hexValue -= 0x8000;
		
		if(isSigned) {
			printString = title + "," + (((short)hexValue) * scalar) + "\n";
		}
		else {
			printString = title + "," + (hexValue * scalar) + "\n";
		}
		return printString;
	}
	
	public String hex(byte num) {
		return String.format("%02x", num);
	}
}