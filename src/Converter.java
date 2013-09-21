import java.io.File;


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
}