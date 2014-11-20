package dz.lab.finance.provider.data;

public class FileDownloadedEvent {
	private final String filename;
	
	public FileDownloadedEvent(String filename) {
		this.filename = filename;
	}
	
	public String getFilename() {
		return filename;
	}

	@Override
	public String toString() {
		return "FileDownloadedEvent [filename=" + filename + "]";
	}	
}
