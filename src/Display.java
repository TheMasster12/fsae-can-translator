import java.awt.Color;
import java.awt.Container;
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
				
		inputTextField = new JLabel(new File("").getAbsolutePath());
		//inputTextWrapper.setAlignmentX(Component.CENTER_ALIGNMENT);
		mainPanel.add(inputTextField);
		
		JButton inputButton = new JButton("Select File to Convert");
		inputButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				showInputChooser();
			}
		});
		//inputButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		mainPanel.add(inputButton);
		
		outputTextField = new JLabel(new File("").getAbsolutePath());
		mainPanel.add(outputTextField);
		
		JButton outputButton = new JButton("Select Output File");
		outputButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				showOutputChooser();
			}
		});
		mainPanel.add(outputButton);
		
		convertProgressBar = new JProgressBar();
		convertProgressBar.setBackground(Color.red);
		convertProgressBar.setForeground(Color.green);
		mainPanel.add(convertProgressBar);
		
		normalizeProgressBar = new JProgressBar();
		normalizeProgressBar.setBackground(Color.red);
		normalizeProgressBar.setForeground(Color.green);
		mainPanel.add(normalizeProgressBar);
		
		outputProgressBar = new JProgressBar();
		outputProgressBar.setBackground(Color.red);
		outputProgressBar.setForeground(Color.green);
		mainPanel.add(outputProgressBar);
		
		JButton convertButton = new JButton("Convert Selected File");
		convertButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				resetDisplay();
				new RunThread().start();
			}
		});
		mainPanel.add(convertButton);
		
		timeElapsedField = new JLabel();
		mainPanel.add(timeElapsedField);
		
		Container container = getContentPane();
		container.add(mainPanel);
		
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setSize(600,300);
		this.setTitle("FSAE Data Converter - Andrew Mass");
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
			inputTextField.setText(file.getAbsolutePath());
			converter.setInputFile(fileChooser.getSelectedFile());
		}
	}
	
	private void showOutputChooser() {
		JFileChooser fileChooser = new JFileChooser(new File("").getAbsoluteFile());
		int returnVal = fileChooser.showSaveDialog(mainPanel);
		
		if(returnVal == JFileChooser.APPROVE_OPTION) {
			File file = fileChooser.getSelectedFile();
			outputTextField.setText(file.getAbsolutePath());
			converter.setOutputFile(fileChooser.getSelectedFile());
		}
	}
	
	public void setProgress(int which, int progress) {
		switch(which) {
			case 0:
				convertProgressBar.setValue(progress);
				mainPanel.invalidate();
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
