package hub;

import java.net.InetAddress;

public class User {
	
	private InetAddress addr;
	private String username;
	
	public User(InetAddress addr, String username) {
		this.addr = addr;
		this.username = username;
	}
	
	public InetAddress getAddr() {
		return this.addr;
	}
	
	public String getUsername() {
		return this.username;
	}
}
