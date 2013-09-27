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
	private Map<String, MessageType> msgdat;
	
	public Converter() {
		//display = new Display(this);
		//display.setVisible(true);
		initMessageInfo();
		setInputFile(new File(new File("").getAbsolutePath() + "/2000.TXT"));
		convert(null);
	}
	
	public void initMessageInfo() {
		msgdat = new HashMap<String, MessageType>();
		msgdat.put("0050", new MessageType("0050", 4, "Rear Hub", new SubMessage[] {new SubMessage("Suspension Pot RL", false, 0.012207, "mm"), new SubMessage("Suspension Pot RR", false, 0.0148866, "mm")}));
		msgdat.put("0060", new MessageType("0060", 4, "Front Hub", new SubMessage[] {new SubMessage("Suspension Pot FR", false, 0.012207, "mm"), new SubMessage("Suspension Pot FL", false, 0.012207, "mm")}));
		msgdat.put("0070", new MessageType("0070", 8, "Accelerometer", new SubMessage[] {new SubMessage("Yaw Rate", true, 0.005, "deg/sec"), new SubMessage("Reserved", true, 1, ""), new SubMessage("Accel Y Axis", true, 0.0001274, "g"), new SubMessage("Unused", true, 1, "")}));
		msgdat.put("0080", new MessageType("0080", 8, "Accelerometer", new SubMessage[] {new SubMessage("Yaw Accel", true, 0.0125, "deg/sec^2"), new SubMessage("Reserved", true, 1, ""), new SubMessage("Accel x Axis", true, 0.0001274, "g"), new SubMessage("Unused", true, 1, "")}));
		msgdat.put("0100", new MessageType("0100", 0, "Rear Hub", new SubMessage[] {}));
		msgdat.put("0110", new MessageType("0110", 6, "Front Hub", new SubMessage[] {new SubMessage("Brake Pressure 1", false, 0.7629395, "psi"), new SubMessage("Brake Pressure 0", false, 0.7629395, "psi"), new SubMessage("Steer. Angle", false, 0.0878906, "deg"), new SubMessage("Unused", true, 1, "")}));
		msgdat.put("0200", new MessageType("0200", 8, "Motec", new SubMessage[] {new SubMessage("RPM", true, 1, "rpm"), new SubMessage("Throttle Position", true, 0.1, "%"), new SubMessage("Oil Pressure", true, 0.1, "psi"), new SubMessage("Oil Temperature", true, 0.1, "C")}));
		msgdat.put("0201", new MessageType("0201", 8, "Motec", new SubMessage[] {new SubMessage("Engine Temperature", true, 0.1, "C"), new SubMessage("Lambda", true, 0.001, "La"), new SubMessage("Manifold Pressure", true, 0.1, "kPa"), new SubMessage("Battery Voltage", true, 0.01, "V")}));
		msgdat.put("0202", new MessageType("0202", 8, "Motec", new SubMessage[] {new SubMessage("Wheel Speed FL", true, 0.1, "MPH"), new SubMessage("Wheel Speed FR", true, 0.1, "MPH"), new SubMessage("Wheel Speed RR", true, 0.1, "MPH"), new SubMessage("Wheel Speed RL", true, 0.1, "MPH")}));
		msgdat.put("0203", new MessageType("0203", 2, "Motec", new SubMessage[] {new SubMessage("Ground Speed", true, 0.1, "MPH"), new SubMessage("Unused", true, 0.1, "")}));

	}
	
	public void setInputFile(File file) {
		this.inputFile = file;
	}
	
	public void setOutputFile(File file) {
		this.outputFile = file;
	}
	
	public String hex(byte num) {
		return String.format("%02x", num);
	}
	
	public void convert(JProgressBar progressBar) {
		if(inputFile == null || !inputFile.canRead() || !inputFile.exists() || inputFile.isDirectory() || !inputFile.isFile()) {
			//JOptionPane.showConfirmDialog(progressBar.getParent(), "Please choose a valid input file.", "Invalid File Error", JOptionPane.ERROR_MESSAGE);
		}
				
		byte[] data = null;
		try {
			data = Files.readAllBytes(inputFile.toPath());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		for(int i=0; i<1000; i++) {
			String msgId = hex(data[i+1]) + hex(data[i]);
			if(msgdat.containsKey(msgId)) {
				String printString = "";
				byte[] msgBytes = new byte[msgdat.get(msgId).getLength()];
				for(int j=0; j<msgdat.get(msgId).getLength();j++) {
					msgBytes[j] = data[j+i+2];
				}
				printString += msgdat.get(msgId).translateData(msgBytes);
				i = i + msgdat.get(msgId).getLength() + 6;
				
				System.out.println(printString);
				i--; // Handle increment on our own
			} 
			else {
				System.out.println(msgId + " Message Code Not Found");
				return;
			}
		}	
	}
}