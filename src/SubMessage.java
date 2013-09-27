public class SubMessage {
	private String title;
	private boolean isSigned;
	private double scalar;
	private String units;

	public SubMessage(String title, boolean isSigned, double scalar, String units) {
		this.title = title;
		this.isSigned = isSigned;
		this.scalar = scalar;
		this.units = units;
	}
	
	public String translateData(byte one, byte two) {
		String printString = "";
		if(title.equals("Unused") || title.equals("Reserved")) return printString;
		if(isSigned || !isSigned) {
			printString = title + "," + (Integer.parseInt(hex(one) + hex(two), 16) * scalar) + "\n";
		}
		return printString;
	}
	
	public String hex(byte num) {
		return String.format("%02x", num);
	}
}