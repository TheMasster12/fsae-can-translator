import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.JOptionPane;
import javax.swing.JProgressBar;


public class Converter {
	//private Display display;
	private File inputFile;
	private File outputFile;
	private Map<String, MessageType> msgdat;
	
	public Converter() {
		//display = new Display(this);
		//display.setVisible(true);
		initMessageInfo();
		setInputFile(new File(new File("").getAbsolutePath() + "/sample-data/1000.data"));
		setOutputFile(new File(new File("").getAbsolutePath() + "/sample-data/1000.out"));
	}
	
	public void begin() {
		long time = System.currentTimeMillis();
		convert(null);
		System.out.println("Time Elapsed " + (System.currentTimeMillis() - time) / 1000.0 + "s");
	}
	
	public void initMessageInfo() {
		msgdat = new TreeMap<String, MessageType>();
		msgdat.put("0050", new MessageType("0050", 4, new SubMessage[] {new SubMessage("Suspension Position RL", false, false, 0.012207f, "mm"), new SubMessage("Suspension Position RR", false, false, 0.0148866f, "mm")}));
		msgdat.put("0060", new MessageType("0060", 4, new SubMessage[] {new SubMessage("Suspension Position FR", false, false, 0.012207f, "mm"), new SubMessage("Suspension Position FL", false, false, 0.012207f, "mm")}));
		msgdat.put("0070", new MessageType("0070", 8, new SubMessage[] {new SubMessage("Yaw Rate", true, false, 0.005f, "deg/sec"), new SubMessage("Reserved", true, false, 1, ""), new SubMessage("Lateral Acceleration", true, false, 0.0001274f, "g"), new SubMessage("Unused", true, false, 1, "")}));
		msgdat.put("0080", new MessageType("0080", 8, new SubMessage[] {new SubMessage("Yaw Acceleration", true, false, 0.0125f, "deg/sec^2"), new SubMessage("Reserved", true, false, 1, ""), new SubMessage("Longitudinal Acceleration", true, false, 0.0001274f, "g"), new SubMessage("Unused", true, false, 1, "")}));
		msgdat.put("0100", new MessageType("0100", 0, new SubMessage[] {}));
		msgdat.put("0110", new MessageType("0110", 6, new SubMessage[] {new SubMessage("Brake Pressure 1", false, false, 0.7629395f, "psi"), new SubMessage("Brake Pressure 0", false, false, 0.7629395f, "psi"), new SubMessage("Steering Angle", false, false, 0.0878906f, "deg"), new SubMessage("Unused", true, false, 1, "")}));
		msgdat.put("0200", new MessageType("0200", 8, new SubMessage[] {new SubMessage("RPM", true, true, 1, "rpm"), new SubMessage("Throttle Position", true, true, 0.1f, "%"), new SubMessage("Oil Pressure", true, true, 0.1f, "psi"), new SubMessage("Oil Temperature", true, true, 0.1f, "C")}));
		msgdat.put("0201", new MessageType("0201", 8, new SubMessage[] {new SubMessage("Engine Temperature", true, true, 0.1f, "C"), new SubMessage("Lambda", true, true, 0.001f, "La"), new SubMessage("Manifold Pressure", true, true, 0.1f, "kPa"), new SubMessage("Battery Voltage", true, true, 0.01f, "V")}));
		msgdat.put("0202", new MessageType("0202", 8, new SubMessage[] {new SubMessage("Wheel Speed FL", true, true, 0.1f, "MPH"), new SubMessage("Wheel Speed FR", true, true, 0.1f, "MPH"), new SubMessage("Wheel Speed RR", true, true, 0.1f, "MPH"), new SubMessage("Wheel Speed RL", true, true, 0.1f, "MPH")}));
		msgdat.put("0203", new MessageType("0203", 2, new SubMessage[] {new SubMessage("Ground Speed", true, true, 0.1f, "MPH"), new SubMessage("Unused", true, true, 0.1f, "")}));
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
			JOptionPane.showConfirmDialog(progressBar.getParent(), "Please choose a valid input file.", "Invalid File Error", JOptionPane.ERROR_MESSAGE);
		}
				
		byte[] data = null;
		try {
			data = Files.readAllBytes(inputFile.toPath());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		try {
			FileWriter fw = new FileWriter(outputFile.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			
			int i=0;
			while(true) {
				if(i>= data.length) break;
				String msgId = hex(data[i+1]) + hex(data[i]);
				if(msgdat.containsKey(msgId)) {
					String printString = "";
					byte[] msgBytes = new byte[msgdat.get(msgId).getLength()];
					for(int j=0; j<msgdat.get(msgId).getLength();j++) {
						msgBytes[j] = data[j+i+2];
					}
					printString += msgdat.get(msgId).translateData(msgBytes);
					i = i + msgdat.get(msgId).getLength() + 6;
	
					bw.write(printString);
				} 
				else {
					bw.write("Error: Message Code Not Found - " + msgId);
					break;
				}
			}
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}