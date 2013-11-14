/**
 * A model class which represents a type of CAN Message.
 * 
 * @author Andrew Mass
 * @version 1.0.0
 */
public class MessageType {

  // The message's Id which is sent out on the CAN bus.
  private String messageId;

  // Specifies how many SubMessages are contained in this message.
  private int messageLength;

  // A collection of the SubMessages contained by this CAN message.
  private SubMessage[] messageData;

  /**
   * Standard constructor for a MessageType object.
   * 
   * @param id
   *          The message's Id which is sent out on the CAN bus.
   * @param length
   *          The length of this message, which specifies how many SubMessages are contained in this
   *          message.
   * @param data
   *          A collection of the SubMessages contained by this CAN message.
   */
  public MessageType(String id, int length, SubMessage[] data) {
    this.messageId = id;
    this.messageLength = length;
    this.messageData = data;
  }

  /**
   * Takes all the data bytes for this message and returns a float[] which contains the translated
   * values. This method sends most of the heavy lifting to the SubMessage class, but the timestamp
   * is calculated and added to the array.
   * 
   * @param data
   *          Bytes which contain all the SubMessage data contained in this message.
   * @param time
   *          Bytes which contain all the timestamp data for this message.
   * @param numColumns
   *          The total number of columns in the main data array for the application.
   * @return A float[] that contains the translated values from this message which will be inserted
   *         into the main data array for the application.
   */
  public float[] translateData(byte[] data, byte[] time, int numColumns) {

    float timestamp = ((Integer.parseInt(hex(time[3]) + hex(time[2]), 16) * 1.0f)
        + (((Integer.parseInt(hex(time[1]) + hex(time[0]), 16) * 1.0f) / 32768.0f)) - 1.0f);

    float[] values = new float[numColumns];
    for(int i = 0; i < values.length; i++) {
      values[i] = Float.MAX_VALUE;
    }
    values[0] = timestamp;

    for(int i = 0; i < messageLength / 2; i++) {
      if(!(messageData[i].getTitle().equals("Rsrvd") || messageData[i].getTitle().equals("Unused"))) {
        values[messageData[i].getColumnIndex()] = messageData[i].getValue(data[2 * i],
            data[(2 * i) + 1]);
      }
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

  /**
   * Takes the byte input and returns a String representation in hex.
   * 
   * @param num
   *          A number in the form of a byte object.
   * @return A string representation of num in hex format.
   */
  public String hex(byte num) {
    return String.format("%02x", num);
  }
}
