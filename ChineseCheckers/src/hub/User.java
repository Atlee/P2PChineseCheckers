package hub;

import java.net.InetAddress;

public class User {
	
	private InetAddress addr;
	private String username;
	private int sessionKey;
	
	public User(InetAddress addr, String username, int sessionKey) {
		this.addr = addr;
		this.username = username;
		this.sessionKey = sessionKey;
	}
	
	public InetAddress getAddr() {
		return this.addr;
	}
	
	public String getUsername() {
		return this.username;
	}
	
	public int getSessionKey() {
		return sessionKey;
	}
}
