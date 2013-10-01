
public class MessageType {
	private String messageId;
	private int messageLength;
	private SubMessage[] messageData;
	
	public MessageType(String id, int length, SubMessage[] data) {
		this.messageId = id;
		this.messageLength = length;
		this.messageData = data;
	}
	
	public String translateData(byte[] data, byte[] time) {
		float timestamp = (Integer.parseInt(hex(time[3]) + hex(time[2]),16) * 1.0f) + ((Integer.parseInt(hex(time[0]) + hex(time[1]),16) * 1.0f) / 32768.0f) - 1.0f;
		
		String printString = timestamp + " ";
		for(int i=0;i<messageData[0].getColumnIndex();i++) {
			printString += "x ";
		}
		
		for(int i=0;i<messageLength/2;i++) {
			for(int j=messageData[i].getColumnIndex();j<messageData[i].getColumnIndex();j++) {
				printString += "x ";
			}
			printString += timestamp + " " + messageData[i].getValue(data[2*i], data[(2*i)+1]) + "\n";
		}
		return printString;
	}
	
	public int getLength() {
		return this.messageLength;
	}
	
	public String getMessageId() {
		return this.messageId;
	}
	
	public SubMessage[] getSubMessages() {
		return this.messageData;
	}
	
	public String hex(byte num) {
		return String.format("%02x", num);
	}
}
