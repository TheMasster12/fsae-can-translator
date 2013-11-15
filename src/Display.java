import java.awt.Color;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

/**
 * Class which controls all the graphical components of the application. Builds a GUI out of Java
 * components, styles all necessary components by setting instance variables, provides access to the
 * controls for the translation, and shows data about the status of a currently running translation
 * and the finished translation.
 * 
 * @author Andrew Mass
 * @version 1.0.1
 */
public class Display extends JFrame {

  /**
   * Auto-generated serialization Id.
   */
  private static final long serialVersionUID = 3366543860445759856L;

  private Translator translator;

  private JPanel mainPanel;

  private JLabel inputTextField;
  private JLabel outputTextField;
  private JLabel timeElapsedField;

  private JProgressBar convertProgressBar;
  private JProgressBar normalizeProgressBar;
  private JProgressBar outputProgressBar;

  /**
   * Main constructor for Display class. Builds GUI from Java components. Sets click listeners for
   * all buttons.
   * 
   * @param parent The instance of the Translator class that generated this instance of Display.
   */
  public Display(Translator parent) {
    this.translator = parent;

    // Instantiates the main JPanel and gives it a GridBagLayout which is a type of table layout
    mainPanel = new JPanel();
    mainPanel.setLayout(new GridBagLayout());

    /*
     * This instance of GridBagConstraints sets the width, height, position, and more values for the
     * component that we are currently placing into the grid. Values that do not change for
     * inserting the next component do not need to be reset.
     */
    GridBagConstraints c = new GridBagConstraints();
    c.gridx = 0;
    c.gridy = 0;
    c.gridwidth = 2;
    c.gridheight = 1;
    c.fill = GridBagConstraints.HORIZONTAL;

    // Adds the inputTextField JLabel to the grid with the initial constraints.
    inputTextField = new JLabel("Input File: " + new File("").getAbsolutePath());
    mainPanel.add(inputTextField, c);

    // Changes constraints and adds the outputTextField to the grid.
    outputTextField = new JLabel("Output File: " + new File("").getAbsolutePath());
    c.gridy = 1;
    mainPanel.add(outputTextField, c);

    /*
     * Sets listener for the inputButton. When clicked, it shows the input chooser pop-up. Changes
     * constraints and adds the inputButton to the grid.
     */
    JButton inputButton = new JButton("Select File to Translate");
    inputButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        showInputChooser();
      }
    });
    c.gridwidth = 1;
    c.gridy = 2;
    c.weightx = 1;
    mainPanel.add(inputButton, c);

    /*
     * Sets listener for the outputButton When clicked, it shows the output chooser pop-up. Changes
     * constraints and adds the outputButton to the grid.
     */
    JButton outputButton = new JButton("Select File to Output");
    outputButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        showOutputChooser();
      }
    });
    c.gridy = 2;
    c.gridx = 1;
    mainPanel.add(outputButton, c);

    /*
     * Instantiates the convertProgressBar, sets it's colors, changes the constraints, and adds it
     * to the grid.
     */
    convertProgressBar = new JProgressBar();
    convertProgressBar.setBackground(Color.red);
    convertProgressBar.setForeground(Color.green);
    c.gridwidth = 2;
    c.gridx = 0;
    c.gridy = 3;
    mainPanel.add(convertProgressBar, c);

    /*
     * Instantiates the normalizeProgressBar, sets it's colors, changes the constraints, and adds it
     * to the grid.
     */
    normalizeProgressBar = new JProgressBar();
    normalizeProgressBar.setBackground(Color.red);
    normalizeProgressBar.setForeground(Color.green);
    c.gridy = 4;
    mainPanel.add(normalizeProgressBar, c);

    /*
     * Instantiates the outputProgressBar, sets it's colors, changes the constraints, and adds it to
     * the grid.
     */
    outputProgressBar = new JProgressBar();
    outputProgressBar.setBackground(Color.red);
    outputProgressBar.setForeground(Color.green);
    c.gridy = 5;
    mainPanel.add(outputProgressBar, c);

    /*
     * Sets listener for the translateButton. When clicked, it will first reset the display to the
     * initial state (in case the user runs the translation more than once), and then start the
     * RunThread (See RunThread class). Changes constraints and adds the translateButton to the
     * grid.
     */
    JButton translateButton = new JButton("Translate Selected File");
    translateButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        resetDisplay();
        new RunThread().start();
      }
    });
    c.gridy = 6;
    mainPanel.add(translateButton, c);

    // Instantiates the timeElapsedField JLabel, changes constraints, and adds it to the grid.
    timeElapsedField = new JLabel();
    c.gridy = 7;
    mainPanel.add(timeElapsedField, c);

    // Adds the main JPanel into the content pane.
    Container container = getContentPane();
    container.add(mainPanel);

    // Sets some default behavior for the GUI window.
    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    this.setSize(750, 170);
    this.setTitle("FSAE CAN Translator - Illini Motorsports - Andrew Mass");
  }

  /**
   * Resets the GUI so that the translation can be run again. Sets the progress bars to their
   * minimum values and clears the time elapsed label.
   */
  private void resetDisplay() {
    convertProgressBar.setValue(0);
    normalizeProgressBar.setValue(0);
    outputProgressBar.setValue(0);
    timeElapsedField.setText("");
  }

  /**
   * Opens a pop-up for the user which allows them to select a file which they would like to use as
   * the input file for the application.
   */
  private void showInputChooser() {
    // Opens the file chooser in the application's running directory
    JFileChooser fileChooser = new JFileChooser(new File("").getAbsoluteFile());
    int returnVal = fileChooser.showOpenDialog(mainPanel);

    // True if the user clicks 'OK' instead of 'Cancel'
    if(returnVal == JFileChooser.APPROVE_OPTION) {
      File file = fileChooser.getSelectedFile();
      inputTextField.setText("Input File: " + file.getAbsolutePath());
      translator.setInputFile(fileChooser.getSelectedFile());
    }
  }

  /**
   * Opens a pop-up for the user which allows them to select a file which they would like to use as
   * the output file for the application.
   */
  private void showOutputChooser() {
    // Opens the file chooser in the application's running directory
    JFileChooser fileChooser = new JFileChooser(new File("").getAbsoluteFile());
    int returnVal = fileChooser.showSaveDialog(mainPanel);

    // True if the user clicks 'OK' instead of 'Cancel'
    if(returnVal == JFileChooser.APPROVE_OPTION) {
      File file = fileChooser.getSelectedFile();
      outputTextField.setText("Output File: " + file.getAbsolutePath());
      translator.setOutputFile(fileChooser.getSelectedFile());
    }
  }

  /**
   * Sets a specific progress bar to the specified progress value.
   * 
   * @param which Integer Id of which progress bar to change the value of.
   * @param progress The value to which the selected progress bar should be set to.
   */
  public void setProgress(int which, int progress) {
    switch(which) {
      case 0:
        convertProgressBar.setValue(progress);
        return;
      case 1:
        normalizeProgressBar.setValue(progress);
        return;
      case 2:
        outputProgressBar.setValue(progress);
        return;
    }
  }

  /**
   * Helper function which displays an error message using the standard JOptionPane class.
   * 
   * @param title The title to display for the error message.
   * @param message The description of the error to display for the error message.
   */
  public void error(String title, String message) {
    JOptionPane.showMessageDialog(mainPanel, message, title, JOptionPane.ERROR_MESSAGE);
  }

  /**
   * Sets the value of the timeElapsedField JLabel to inform the user of how long the translation
   * took.
   * 
   * @param time The time that the application took to convert the input file.
   */
  public void showTimeElapsed(double time) {
    timeElapsedField.setText("Time Elapsed: " + time + "s");
  }

  /**
   * Class which extends the standard Java Thread class. All translation work is done in this thread
   * so we take the heavy processing off of the main thread. This is generally a good practice, and
   * it allows the GUI to update while the translation is running.
   * 
   * @author Andrew Mass
   * @version 1.0.0
   */
  public class RunThread extends Thread {

    /**
     * Starts the thread and tells the instance of the Translator class to start the translation.
     * 
     * @see java.lang.Thread#run()
     */
    public void run() {
      translator.begin();
    }
  }
}
