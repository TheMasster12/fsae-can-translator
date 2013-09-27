
public class MessageType {
	private String msgId;
	private int messageLength;
	private String messageOrigin;
	private SubMessage[] messageData;
	
	public MessageType(String id, int length, String origin, SubMessage[] data) {
		this.msgId = id;
		this.messageLength = length;
		this.messageOrigin = origin;
		this.messageData = data;
	}
	
	public String translateData(byte[] data) {
		String printString = msgId + " " + messageOrigin + "-";
		for(int i=0;i<messageLength/2;i++) {
			printString += messageData[i].translateData(data[2*i], data[(2*i)+1]);
		}
		return printString;
	}
	
	public int getLength() {
		return this.messageLength;
	}
}
