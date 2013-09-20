import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;

public class Display extends JFrame {
	private static final long serialVersionUID = 3366543860445759856L;
	private Converter converter;
	private JPanel mainPanel;
	private JTextField inputTextField;
	private JTextField outputTextField;
	private JProgressBar progressBar;
	
	public Display(Converter parent) {
		this.converter = parent;
		mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
				
		inputTextField = new JTextField(new File("").getAbsolutePath());
		inputTextField.setBorder(BorderFactory.createCompoundBorder(new EmptyBorder(10, 10, 10, 10), new EtchedBorder()));
		mainPanel.add(inputTextField);
		
		JButton inputButton = new JButton("Select File to Convert");
		inputButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				showInputChooser();
			}
		});
		inputButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		mainPanel.add(inputButton);
		
		outputTextField = new JTextField(new File("").getAbsolutePath() + "/output.txt");
		mainPanel.add(outputTextField);
		
		JButton outputButton = new JButton("Select Output File");
		outputButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				showOutputChooser();
			}
		});
		mainPanel.add(outputButton);
		
		progressBar = new JProgressBar();
		progressBar.setBackground(Color.red);
		progressBar.setForeground(Color.green);
		
		progressBar.setBorder(new EmptyBorder(30,30,30,30));
		mainPanel.add(progressBar);
		
		JButton convertButton = new JButton("Convert Selected File");
		convertButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("Convert!");
			}
		});
		mainPanel.add(convertButton);
		
		Container container = getContentPane();
		container.add(mainPanel);
		
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setSize(600,300);
		this.setTitle("FSAE Data Converter - Andrew Mass");
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
		
	}
}
