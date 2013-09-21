import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import javax.swing.JOptionPane;
import javax.swing.JProgressBar;


public class Converter {
	private Display display;
	private File inputFile;
	private File outputFile;
	
	public Converter() {
		display = new Display(this);
		display.setVisible(true);
	}
	
	public void setInputFile(File file) {
		this.inputFile = file;
	}
	
	public void setOutputFile(File file) {
		this.outputFile = file;
	}
	
	public String hex(int num) {
		String temp = Integer.toHexString(num);
		if(temp.length() < 2) temp = "0" + temp;
		return temp;
	}
	
	public void convert(JProgressBar progressBar) {
		if(inputFile == null || !inputFile.canRead() || !inputFile.exists() || inputFile.isDirectory() || !inputFile.isFile()) {
			JOptionPane.showConfirmDialog(progressBar.getParent(), "Please choose a valid input file.", "Invalid File Error", JOptionPane.ERROR_MESSAGE);
		}
				
		byte[] data = null;
		try {
			data = Files.readAllBytes(inputFile.toPath());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		for(int i=0; i<1; i++) {
			if(hex(data[i] + data[i+1]).equals("60")) {
				System.out.println(hex(data[i]) + " " + hex(data[i+1]) + " " + hex(data[i+2]) + " " + hex(data[i+3]) + " " + hex(data[i+4]) + " " + hex(data[i+5]));
				System.out.println("Front Hub - " + "Sus Pot FR: " + (Integer.parseInt(hex(data[i+2]) + hex(data[i+3]), 16)) + " Sus Pot FL: " + Integer.parseInt(hex(data[i+4]) + hex(data[i+5]), 16));
			}
		}	
	}
}