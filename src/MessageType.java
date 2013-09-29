
public class MessageType {
	private String messageId;
	private int messageLength;
	private SubMessage[] messageData;
	
	public MessageType(String id, int length, SubMessage[] data) {
		this.messageId = id;
		this.messageLength = length;
		this.messageData = data;
	}
	
	public String translateData(byte[] data) {
		String printString = "";
		for(int i=0;i<messageLength/2;i++) {
			printString += messageData[i].translateData(data[2*i], data[(2*i)+1]);
		}
		return printString;
	}
	
	public int getLength() {
		return this.messageLength;
	}
	
	public String getMessageId() {
		return this.messageId;
	}
}
