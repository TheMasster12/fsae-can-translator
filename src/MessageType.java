
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
		//time = theTime[3] * 256. + theTime[2] * 1. + (theTime[0] * 1. + theTime[1] * 256.) / 32768. - 1.;
		float timestamp = (Integer.parseInt(hex(time[3]) + hex(time[2]),16) * 1.0f) + ((Integer.parseInt(hex(time[0]) + hex(time[1]),16) * 1.0f) / 32768.0f) - 1.0f;
		
		String printString = "";
		for(int i=0;i<messageLength/2;i++) {
			printString += messageData[i].translateData(data[2*i], data[(2*i)+1], timestamp);
		}
		return printString;
	}
	
	public int getLength() {
		return this.messageLength;
	}
	
	public String getMessageId() {
		return this.messageId;
	}
	
	public String hex(byte num) {
		return String.format("%02x", num);
	}
}
