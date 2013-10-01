
public class MessageType {
	private String messageId;
	private int messageLength;
	private SubMessage[] messageData;
	
	public MessageType(String id, int length, SubMessage[] data) {
		this.messageId = id;
		this.messageLength = length;
		this.messageData = data;
	}
	
	public float[] translateData(byte[] data, byte[] time, int numColumns) {
		float timestamp = (Integer.parseInt(hex(time[3]) + hex(time[2]),16) * 1.0f) + ((Integer.parseInt(hex(time[0]) + hex(time[1]),16) * 1.0f) / 32768.0f) - 1.0f;

		float[] values = new float[numColumns];
		for(int i=0;i<values.length;i++) {
			values[i] = Float.MAX_VALUE;
		}
		values[0] = timestamp;
		
		for(int i=0;i<messageLength/2;i++) {
			values[messageData[i].getColumnIndex()] = messageData[i].getValue(data[2*i], data[(2*i)+1]);
		}
		return values;
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
