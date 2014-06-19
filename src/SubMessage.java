/**
 * A model class which represents a SubMessage. Each CAN message contains one or more SubMessages which each contain
 * information about a specific data point. For example, a temperature CAN message will contain several SubMessages
 * which each contain one sensor's temperature, the units of the measurement, and some other information.
 * 
 * @author Andrew Mass
 * @version 1.0.1
 */
public class SubMessage {

  private final String title;
  private final boolean isSigned;
  private final boolean isBigEndian;
  private final String units;
  private final float scalar;
  private final float offset;

  /**
   * Keeps track of where this type of SubMessage is stored in the 2D grid that holds all the data. All SubMessages of
   * the same type will have the same columnIndex.
   */
  private int columnIndex;

  /**
   * Standard constructor for a SubMessage object.
   * 
   * @param title Name of the SubMessage.
   * @param isSigned Whether or not the value is an signed integer.
   * @param isBigEndian Whether or not the value is big endian.
   * @param scalar This is multiplied to the value sent over CAN to obtain the correct measurement value.
   * @param offset This is subtracted from the value sent over CAN. Only necessary for some SubMessages.
   * @param units The units of the value.
   */
  public SubMessage(String title, boolean isSigned, boolean isBigEndian, float scalar, float offset, String units) {
    this.title = title;
    this.isSigned = isSigned;
    this.isBigEndian = isBigEndian;
    this.scalar = scalar;
    this.offset = offset;
    this.units = units;
  }

  /**
   * Takes the two data bytes which hold the data sent over CAN for a specific SubMessage and returns a value which is
   * offset and scaled to correctly show the intended measurement.
   * 
   * @param one The first data byte in the sequence of two bytes.
   * @param two The second data byte in the sequence of two bytes.
   * @return The intended measurement.
   */
  public float getValue(byte one, byte two) {
    // Concatenates the bytes in either big-endian or little-endian format.
    String hexString = isBigEndian ? hex(one) + hex(two) : hex(two) + hex(one);
    int hexValue = Integer.parseInt(hexString, 16);

    hexValue = (int) ((hexValue) - offset);

    // If the value is a signed integer, cast it to a short before scaling.
    return isSigned ? (((short) hexValue) * scalar) : (hexValue * scalar);
  }

  public float getValue(byte one, byte two, byte three, byte four) {
    String hexString = isBigEndian ? hex(one) + hex(two) + hex(three) + hex(four) : hex(four) + hex(three) + hex(two) +
        hex(one);
    long hexValue = Long.parseLong(hexString, 16);
    hexValue = (long) ((hexValue) - offset);
    return isSigned ? (((int) hexValue) * scalar) : (hexValue * scalar);
  }

  /**
   * Takes the byte input and returns a String representation in hex.
   * 
   * @param num A number in the form of a byte object.
   * @return A string representation of num in hex format.
   */
  public String hex(byte num) {
    return String.format("%02x", num);
  }

  public int getColumnIndex() {
    return this.columnIndex;
  }

  public void setColumnIndex(int val) {
    this.columnIndex = val;
  }

  public String getTitle() {
    return this.title;
  }

  public String getUnits() {
    return this.units;
  }
}
