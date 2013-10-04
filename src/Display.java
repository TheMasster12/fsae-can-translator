import java.awt.Color;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

public class Display extends JFrame {
	private static final long serialVersionUID = 3366543860445759856L;
	private Converter converter;
	
	private JPanel mainPanel;
	
	private JLabel inputTextField;
	private JLabel outputTextField;
	private JLabel timeElapsedField;
	
	private JProgressBar convertProgressBar;
	private JProgressBar normalizeProgressBar;
	private JProgressBar outputProgressBar;
	
	public Display(Converter parent) {
		this.converter = parent;
		mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		mainPanel.setLayout(new GridBagLayout());
		
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 2;
		c.gridheight = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
				
		inputTextField = new JLabel("Input File: " + new File("").getAbsolutePath());
		mainPanel.add(inputTextField, c);
		
		outputTextField = new JLabel("Output File: " + new File("").getAbsolutePath());
		c.gridy = 1;
		mainPanel.add(outputTextField, c);
		
		JButton inputButton = new JButton("Select File to Convert");
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
		
		convertProgressBar = new JProgressBar();
		convertProgressBar.setBackground(Color.red);
		convertProgressBar.setForeground(Color.green);
		c.gridwidth = 2;
		c.gridx = 0;
		c.gridy = 3;
		mainPanel.add(convertProgressBar, c);
		
		normalizeProgressBar = new JProgressBar();
		normalizeProgressBar.setBackground(Color.red);
		normalizeProgressBar.setForeground(Color.green);
		c.gridy = 4;
		mainPanel.add(normalizeProgressBar, c);
		
		outputProgressBar = new JProgressBar();
		outputProgressBar.setBackground(Color.red);
		outputProgressBar.setForeground(Color.green);
		c.gridy = 5;
		mainPanel.add(outputProgressBar, c);
		
		JButton convertButton = new JButton("Convert Selected File");
		convertButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				resetDisplay();
				new RunThread().start();
			}
		});
		c.gridy = 6;
		mainPanel.add(convertButton, c);
		
		timeElapsedField = new JLabel();
		c.gridy = 7;
		mainPanel.add(timeElapsedField, c);
		
		Container container = getContentPane();
		container.add(mainPanel);
		
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setSize(750, 170);
		this.setTitle("FSAE CAN Translator - Andrew Mass");
	}
	
	private void resetDisplay() {
		convertProgressBar.setValue(0);
		normalizeProgressBar.setValue(0);
		outputProgressBar.setValue(0);
		timeElapsedField.setText("");
	}
	
	private void showInputChooser() {
		JFileChooser fileChooser = new JFileChooser(new File("").getAbsoluteFile());
		int returnVal = fileChooser.showOpenDialog(mainPanel);
		
		if(returnVal == JFileChooser.APPROVE_OPTION) {
			File file = fileChooser.getSelectedFile();
			inputTextField.setText("Input File: " + file.getAbsolutePath());
			converter.setInputFile(fileChooser.getSelectedFile());
		}
	}
	
	private void showOutputChooser() {
		JFileChooser fileChooser = new JFileChooser(new File("").getAbsoluteFile());
		int returnVal = fileChooser.showSaveDialog(mainPanel);
		
		if(returnVal == JFileChooser.APPROVE_OPTION) {
			File file = fileChooser.getSelectedFile();
			outputTextField.setText("Output File: " + file.getAbsolutePath());
			converter.setOutputFile(fileChooser.getSelectedFile());
		}
	}
	
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
	
	public void error(String title, String message) {
		JOptionPane.showMessageDialog(mainPanel, message, title, JOptionPane.ERROR_MESSAGE);
	}
	
	public void showTimeElapsed(double time) {
		timeElapsedField.setText("Time Elapsed: " + time + "s");
	}
	
	public class RunThread extends Thread {
		public void run() {
			converter.begin();
		}
	}
}
