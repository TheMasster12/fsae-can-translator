import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;

/**
 * Class which handles the implementation of the translation.
 * 
 * @author Andrew Mass
 * @version 1.0.0
 */
public class Translator {

  private Display display;

  private File inputFile;
  private File outputFile;

  private Map<String, Message> messageData;

  private ArrayList<String> axis;
  private ArrayList<float[]> values;

  private int convertProgress;
  private int normalizeProgress;
  private int printProgress;

  /**
   * Standard constructor for the Translator class. Creates a new display and runs initMessageInfo()
   */
  public Translator() {
    display = new Display(this);
    display.setVisible(true);
    if(!initMessageInfo())
      System.exit(0);
  }

  /**
   * Looks for the configuration file. If found, constructs a series of Message objects each with an
   * array of SubMessage objects. Populates instance variables based on data from the configuration
   * file.
   * 
   * @return Whether the initialization was successful or unsuccessful.
   */
  public boolean initMessageInfo() {
    File configFile = new File(new File("").getAbsolutePath() + "/config.txt");
    if(configFile == null || !configFile.canRead() || !configFile.exists()
        || configFile.isDirectory() || !configFile.isFile()) {
      display
          .error(
              "Configuration File Error",
              "The configuration file cannot be read or found.\n\nPlease place it in the same directory as the program and restart the program.");
      return false;
    }

    Scanner reader;
    try {
      reader = new Scanner(configFile);
    }
    catch(FileNotFoundException e) {
      display
          .error(
              "Configuration File Error",
              "The configuration file cannot be read or found.\n\nPlease place it in the same directory as the program and restart the program.");
      return false;
    }

    messageData = new HashMap<String, Message>();

    /*
     * This loops through each line of the configuration file and constructs Message objects out of
     * the information contained in the line. The number of SubMessage definitions in each line of
     * the the configuration file will determine the number of SubMessage objects in the array.
     */
    while(reader.hasNextLine()) {
      String temp = reader.nextLine();

      if(!temp.substring(0, 2).equals("//")) {

        // Splits the configuration line by separating by commas.
        String[] data = temp.split(",");

        String msgId = data[0];
        int len = Integer.parseInt(data[1]);

        SubMessage[] subMessages = new SubMessage[len / 2];

        /*
         * All the indices used below rely on the preconditions of the configuration file. Namely,
         * the right number of comma separated values must be present and all the values must be in
         * the exact order as outlined in the configuration file comments.
         */
        for(int i = 0; i < len / 2; i++) {
          if(data.length != (2 + 6 * (len / 2))) {
            display
                .error("Configuration Parsing Error",
                    "Invalid length. Missing sub-messages or there is missing values from a sub-message.");
            reader.close();
            return false;
          }

          if(data[2 + 6 * i].length() > 7) {
            display.error("Configuration Parsing Error",
                "The length of a sub-message title must be less than or equal to 7 characters.");
            reader.close();
            return false;
          }

          if(data[7 + 6 * i].length() > 4) {
            display.error("Configuration Parsing Error",
                "The length of a sub-message unit must be less than or equal to 4 characters.");
            reader.close();
            return false;
          }

          if(!(data[3 + 6 * i].equals("false") || data[3 + 6 * i].equals("true"))
              || !(data[4 + 6 * i].equals("false") || data[4 + 6 * i].equals("true"))) {
            display.error("Configuration Parsing Error",
                "Booleans must have a value of 'true' or 'false'");
            reader.close();
            return false;
          }

          try {
            subMessages[i] = new SubMessage(data[2 + 6 * i], Boolean.parseBoolean(data[3 + 6 * i]),
                Boolean.parseBoolean(data[4 + 6 * i]), Float.parseFloat(data[5 + 6 * i]),
                Float.parseFloat(data[6 + 6 * i]), data[7 + 6 * i]);
          }
          catch(Exception e) {
            display.error("Configuration Parsing Error", "Parsing Error: " + e.getMessage());
            reader.close();
            return false;
          }
        }

        messageData.put(msgId, new Message(msgId, len, subMessages));
      }
    }

    reader.close();
    return true;
  }

  /**
   * This method is called when the Display signals that the user wants the translation to begin.
   * Resets all instance variables, then runs the various translation methods in the proper order.
   */
  public void begin() {
    this.convertProgress = 0;
    this.normalizeProgress = 0;
    this.printProgress = 0;

    long time = System.currentTimeMillis();

    this.axis = prepareAxis();

    if(!prepareFiles())
      return;
    if(!convert())
      return;
    normalize();
    outputValues();

    display.showTimeElapsed((System.currentTimeMillis() - time) / 1000.0);
  }

  /**
   * Prepares the first row of the array, which contains the labels and units for each column.
   * 
   * @return A list of the column labels in the proper order.
   */
  private ArrayList<String> prepareAxis() {
    ArrayList<String> axis = new ArrayList<String>();
    axis.add("Time [s] ");

    int lastColumnUsed = 0;

    Iterator<Entry<String, Message>> iterator = messageData.entrySet().iterator();
    while(iterator.hasNext()) {
      Entry<String, Message> message = iterator.next();
      SubMessage[] subMessages = message.getValue().getSubMessages();

      for(int i = 0; i < subMessages.length; i++) {

        // Make sure we don't add Reserved or Unused columns to our data array.
        if(!(subMessages[i].getTitle().equals("Rsrvd") || subMessages[i].getTitle()
            .equals("Unused"))) {
          lastColumnUsed++;
          message.getValue().getSubMessages()[i].setColumnIndex(lastColumnUsed);
          axis.add(subMessages[i].getTitle() + " [" + subMessages[i].getUnits() + "] ");
        }
      }
    }
    return axis;
  }

  /**
   * Ensures that the input and output files are valid and that the input file exists.
   * 
   * @return A boolean representing the success of the checks.
   */
  private boolean prepareFiles() {
    if(inputFile == null || !inputFile.canRead() || !inputFile.exists() || inputFile.isDirectory()
        || !inputFile.isFile()) {
      display.error("Invalid File Error", "Please choose a valid input file.");
      return false;
    }

    if(outputFile == null) {
      display.error("Invalid File Error", "Please specify an output file.");
      return false;
    }
    return true;
  }

  /**
   * Iterates through each message sent over the CAN bus and converts the raw data into the intended
   * values. After the completion of this method, the array will contain all values known from the
   * data, but no interpolation between messages of the same type will be done. So, many values will
   * still be Float.MAX_VALUE.
   * 
   * @return A boolean representing the success of the conversion.
   */
  private boolean convert() {
    byte[] data = null;
    try {
      data = Files.readAllBytes(inputFile.toPath());
    }
    catch(IOException e) {
      display.error("IO Error", "Please try again or try a different file.");
      return false;
    }

    this.values = new ArrayList<float[]>();

    /*
     * This iterator points to a byte in the data array. It will be incremented depending on the
     * number of subMessages in the current message.
     */
    int i = 0;
    while(true) {
      try {
        if(i >= data.length)
          break;

        int temp = (int) (Math.floor(((i / (float) data.length) * 100.0f)));
        if(temp > this.convertProgress)
          setProgress(0, temp);

        String msgId = hex(data[i + 1]) + hex(data[i]);
        if(messageData.containsKey(msgId)) {
          int len = messageData.get(msgId).getLength();

          byte[] timeBytes = new byte[] {data[i + len + 2], data[i + len + 3], data[i + len + 4],
              data[i + len + 5]};

          byte[] msgBytes = new byte[len];
          for(int j = 0; j < len; j++) {
            msgBytes[j] = data[j + i + 2];
          }

          values.add(messageData.get(msgId).translateData(msgBytes, timeBytes, this.axis.size()));
          i = i + len + 6;
        }
        else {
          /*
           * If we come to another message in the stream and can't identify it, then we will have to
           * halt the conversion because the length of the unknown message can't be determined. So,
           * we don't know where the next valid message starts.
           */
          display.error("Conversion Error", "Message Code Not Found - " + msgId);
          break;
        }
      }
      catch(ArrayIndexOutOfBoundsException e) {
        /*
         * If we've gone past the end of the array, we should end the loop gracefully without
         * crashing. The only possible negative side-effect of this is losing one CAN message at the
         * end of the stream, which isn't a problem.
         */
        break;
      }
    }

    setProgress(0, 100);
    return true;
  }

  /**
   * Takes the converted data and interpolates all values so that the data is seamless. This is
   * necessary because some sensors are sampled much more quickly than others, leaving gaps in the
   * slowly-sampled data. Darab works by assuming each sensor reports value at every time, so
   * interpolation is required for the data to display properly without gaps.
   */
  private void normalize() {

    /*
     * Searches for the first known sensor value and copies that value from the beginning of the
     * data array to the point where it was found. Essentially interpolates all the sensors which
     * haven't been sampled yet to the first sampled value.
     */
    for(int i = 1; i < values.get(0).length; i++) {
      for(int j = 0; j < values.size(); j++) {
        if(values.get(j)[i] != Float.MAX_VALUE) {
          for(int k = 0; k < j; k++) {
            values.get(k)[i] = values.get(j)[i];
          }
          break;
        }
      }
    }

    /*
     * Interpolates all values from one sample to the next sensor sample. Will replace all unknown
     * values (represented temporarily by Float.MAX_VALUE) with interpolated values.
     */
    for(int i = 1; i < values.get(0).length; i++) {
      int j = 0;
      float val = values.get(0)[i];
      while(j < values.size()) {
        if(values.get(j)[i] == Float.MAX_VALUE)
          values.get(j)[i] = val;
        else
          val = values.get(j)[i];
        j++;
      }
      int temp = (int) (Math.floor(((i / (float) values.get(0).length) * 100.0f)));
      if(temp > this.normalizeProgress)
        setProgress(1, temp);
    }

    /*
     * If we have any left over Float.MAX_VALUE values, set them to 0.0f instead. This may happen if
     * a specific sensor is never sampled.
     */
    for(int i = 0; i < values.get(0).length; i++) {
      for(int j = 0; j < values.size(); j++) {
        if(values.get(j)[i] == Float.MAX_VALUE) {
          values.get(j)[i] = 0.0f;
        }
      }
    }
    setProgress(1, 100);
  }

  /**
   * Writes the converted and normalized values to the output file.
   */
  private void outputValues() {
    try {
      FileWriter fw = new FileWriter(outputFile.getAbsoluteFile());
      BufferedWriter bw = new BufferedWriter(fw, 32768);

      for(String e: axis) {
        bw.write(e + " ");
      }
      bw.write("\n");

      for(int i = 0; i < values.size(); i++) {
        float[] row = values.get(i);

        bw.write(row[0] + " ");
        for(int j = 1; j < row.length; j++) {
          bw.write(row[j] + " ");
        }

        int temp = (int) (Math.floor(((i / (float) values.size()) * 100.0f)));
        if(temp > this.printProgress)
          setProgress(2, temp);

        bw.write("\n");
      }

      bw.flush();
      bw.close();

      setProgress(2, 100);
    }
    catch(IOException e) {
      display.error("IO Error", "Please try again or try a different file.");
      return;
    }
  }

  /**
   * Sets the progress of one specific section of the translation process.
   * 
   * @param which Which section to set the progress of.
   * @param progress The progress to update to.
   */
  private void setProgress(int which, int progress) {
    display.setProgress(which, progress);
    switch(which) {
      case 0:
        this.convertProgress = progress;
        return;
      case 1:
        this.normalizeProgress = progress;
        return;
      case 2:
        this.printProgress = progress;
        return;
    }
  }

  /**
   * Takes the byte input and returns a String representation in hex.
   * 
   * @param num A number in the form of a byte object.
   * @return A string representation of num in hex format.
   */
  private String hex(byte num) {
    return String.format("%02x", num);
  }

  public void setInputFile(File file) {
    this.inputFile = file;
  }

  public void setOutputFile(File file) {
    this.outputFile = file;
  }
}
