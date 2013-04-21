package game;

public class AuditLog {
	String contents;
	
	public void append(String s) {
		contents = contents + s + "\n";
	}

}
