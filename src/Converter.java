import java.io.File;


public class Converter {
	private Display display;
	private File inputFile;
	
	public Converter() {
		display = new Display(this);
		display.setVisible(true);
	}
	
	public void setInputFile(File file) {
		this.inputFile = file;
	}
}