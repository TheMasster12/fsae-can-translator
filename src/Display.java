import java.awt.Color;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class Display extends JFrame {
	private static final long serialVersionUID = 3366543860445759856L;
	private Converter converter;
	private JPanel inputPanel;
	private JTextField inputTextField;
	
	public Display(Converter parent) {
		this.converter = parent;
		
		inputPanel = new JPanel();
		inputPanel.setBackground(Color.green);
		
		inputTextField = new JTextField(new File("").getAbsolutePath());
		inputPanel.add(inputTextField);
		
		JButton inputButton = new JButton("Select File to Convert");
		inputButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				showFileChooser();
			}
		});
		inputPanel.add(inputButton);
		
		
		
		JPanel outputPanel = new JPanel();
		outputPanel.setBackground(Color.blue);
		
		JPanel progressPanel = new JPanel();
		
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		mainPanel.add(inputPanel);
		mainPanel.add(outputPanel);
		mainPanel.add(progressPanel);
		
		Container container = getContentPane();
		container.add(mainPanel);
		
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setSize(1000,500);
		this.setTitle("FSAE Data Converter - Andrew Mass");
	}
	
	private void showFileChooser() {
		JFileChooser fileChooser = new JFileChooser(new File("").getAbsoluteFile());
		int returnVal = fileChooser.showOpenDialog(inputPanel);
		
		if(returnVal == JFileChooser.APPROVE_OPTION) {
			File file = fileChooser.getSelectedFile();
			inputTextField.setText(file.getAbsolutePath());
			converter.setInputFile(fileChooser.getSelectedFile());
		}
	}
}
