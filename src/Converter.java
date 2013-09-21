import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JOptionPane;
import javax.swing.JProgressBar;


public class Converter {
	private Display display;
	private File inputFile;
	private File outputFile;
	private Map<String, String[]> msgdat;
	
	public Converter() {
		display = new Display(this);
		display.setVisible(true);
		initMessageInfo();
	}
	
	//msgId, [Sensor Name, Data Byte Length, Subname1?, Scalar1, ScalarUnits1, Subname2, Scalar2, ScalarUnits2]
	public void initMessageInfo() {
		msgdat = new HashMap<String, String[]>();
		
		msgdat.put("0060", new String[] {"Front Hub", "4", "Sus Pot FR", "0.012207", "mm", "Sus Pot FL", "0.012207", "mm"});
		msgdat.put("0070", new String[] {"Accelerometer", "8", "Yaw Rate", "0.005", "deg/sec", "Reserved", "1", "N/A", "Accel Y Axis", ".0001274", "g", "Unused", "1", "N/A"});
		msgdat.put("0080", new String[] {"Accelerometer", "8", "Yaw Accel", "0.0125", "deg/sec^2", "Reserved", "1", "N/A", "Accel X Axis", ".0001274", "g", "Unused", "1", "N/A"});
	}
	
	public void setInputFile(File file) {
		this.inputFile = file;
	}
	
	public void setOutputFile(File file) {
		this.outputFile = file;
	}
	
	public String hex(int num) {
		String temp = Integer.toHexString(num);
		while(temp.length() < 2) temp = "0" + temp;
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
		
		for(int i=0; i<100; i++) {
			String msgId = hex(data[i+1]) + hex(data[i]);
			System.out.println(msgId);
			if(msgdat.containsKey(msgId)) {
				String printString = null;
				
				if(Integer.parseInt(msgdat.get(msgId)[1]) == 0) { 
					
				}
				
				if(Integer.parseInt(msgdat.get(msgId)[1]) == 2) { 
					
				}
				
				if(Integer.parseInt(msgdat.get(msgId)[1]) == 4) { 
					printString = msgdat.get(msgId)[0] + " - " 
						+ msgdat.get(msgId)[2] + ": " + (Integer.parseInt(hex(data[i+2]) + hex(data[i+3]), 16) * Double.parseDouble(msgdat.get(msgId)[3])) + msgdat.get(msgId)[4] + " " 
						+ msgdat.get(msgId)[5] + ": " + (Integer.parseInt(hex(data[i+4]) + hex(data[i+5]), 16) * Double.parseDouble(msgdat.get(msgId)[6])) + msgdat.get(msgId)[7];
					i = i + 10;
				}
				
				if(Integer.parseInt(msgdat.get(msgId)[1]) == 6) { 
					
				}
				
				if(Integer.parseInt(msgdat.get(msgId)[1]) == 8) { 
					System.out.println(data[i+2] + " " + data[i+3]);
					printString = msgdat.get(msgId)[0] + " - " 
						+ msgdat.get(msgId)[2] + ": " + (Integer.parseInt(hex(data[i+2]) + hex(data[i+3]), 16) * Double.parseDouble(msgdat.get(msgId)[3])) + msgdat.get(msgId)[4] + " " 
						+ msgdat.get(msgId)[5] + ": " + (Integer.parseInt(hex(data[i+4]) + hex(data[i+5]), 16) * Double.parseDouble(msgdat.get(msgId)[6])) + msgdat.get(msgId)[7]
						+ msgdat.get(msgId)[8] + ": " + (Integer.parseInt(hex(data[i+6]) + hex(data[i+7]), 16) * Double.parseDouble(msgdat.get(msgId)[9])) + msgdat.get(msgId)[10] + " " 
						+ msgdat.get(msgId)[11] + ": " + (Integer.parseInt(hex(data[i+8]) + hex(data[i+9]), 16) * Double.parseDouble(msgdat.get(msgId)[12])) + msgdat.get(msgId)[13];
					i = i + 14;
				}
				
				System.out.println(printString);
				i--; // Handle increment on our own
			} 
			else {
				System.out.println("Message Code Not Found");
				return;
			}
		}	
	}
}