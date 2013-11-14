/**
 * A model class which represents a SubMessage. Each CAN message contains one or more SubMessages
 * which each contain information about a specific data point. For example, a suspension position
 * CAN message will contain several SubMessages which each contain one wheel's position, the units
 * of the measurement, and some other information.
 * 
 * @author Andrew Mass
 * @version 1.0.0
 */
public class SubMessage {

  // Name of the SubMessage.
  private String title;

  // Whether or not the value is a signed integer.
  private boolean isSigned;

  // Whether or not the value is big endian.
  private boolean isBigEndian;

  // A float which is multiplied to the value sent over CAN to obtain the correct measurement value.
  private float scalar;

  // A String representing the units of the value.
  private String units;

  // A float that is subtracted from the value sent over CAN. Only necessary for some SubMessages.
  private float offset;

  // An integer which keeps track of where this type of SubMessage is stored in the 2D grid that
  // holds all the data. All SubMessages of the same type will have the same columnIndex.
  private int columnIndex;

  /**
   * Standard constructor for a SubMessage object.
   * 
   * @param title
   *          Name of the SubMessage.
   * @param isSigned
   *          Whether or not the value is an signed integer.
   * @param isBigEndian
   *          Whether or not the value is big endian.
   * @param scalar
   *          A float which is multiplied to the value sent over CAN to obtain the correct
   *          measurement value.
   * @param offset
   *          A float which is subtracted from the value sent over CAN. Only necessary for some
   *          SubMessages.
   * @param units
   *          A String representing the units of the value.
   */
  public SubMessage(String title, boolean isSigned, boolean isBigEndian, float scalar,
      float offset, String units) {
    this.title = title;
    this.isSigned = isSigned;
    this.isBigEndian = isBigEndian;
    this.scalar = scalar;
    this.offset = offset;
    this.units = units;
  }

  /**
   * Takes the two data bytes which hold the data sent over CAN for a specific SubMessage and
   * returns a value which is offset and scaled to correctly show the intended measurement.
   * 
   * @param one
   *          The first data byte in the sequence of two bytes.
   * @param two
   *          The second data byte in the sequence of two bytes.
   * @return A float value which is the intended measurement.
   */
  public float getValue(byte one, byte two) {

    // Concatenates the bytes in either big-endian or little-endian format.
    String hexString = isBigEndian ? hex(one) + hex(two) : hex(two) + hex(one);
    int hexValue = Integer.parseInt(hexString, 16);

    // Subtracts the offset from the hexValue (could be 0.0f so no change).
    hexValue = (int) (((float) hexValue) - offset);

    // Scales the value by the scalar float.
    // If it's a signed integer, cast it to a short before scaling.
    return isSigned ? (((short) hexValue) * scalar) : (hexValue * scalar);
  }

  /**
   * Gets the column index of this particular type of SubMessage.
   * 
   * @return An integer representing the column index of this particular type of SubMessage.
   */
  public int getColumnIndex() {
    return this.columnIndex;
  }

  /**
   * Sets the column index of this particular type of SubMessage.
   * 
   * @param val
   *          An integer to set columnIndex equal to.
   */
  public void setColumnIndex(int val) {
    this.columnIndex = val;
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

  /**
   * Gets the title for this SubMessage.
   * 
   * @return The title for this SubMessage.
   */
  public String getTitle() {
    return this.title;
  }

  /**
   * Gets the units for this SubMessage.
   * 
   * @return The units for this SubMessage.
   */
  public String getUnits() {
    return this.units;
  }
}
