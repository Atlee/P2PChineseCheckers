package game;

public class AuditLog {
	String contents = "";
	
	public void append(String s) {
		contents = contents + s + "\n";
	}
	
	public void prepend(String s) {
		contents = s + "\n" + contents;
	}
	
	public byte[] getBytes() {
		return contents.getBytes();
	}
	
	public String getLog() {
		return contents;
	}

}
