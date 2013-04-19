package game;

import java.net.InetAddress;

public class Player {
	
	private final String username;
	private final int index;

	public Player(String username, int index) {
		this.username = username;
		this.index = index;
	}
	
	public int getIndex() {
		return index;
	}
	
	@Override
	public boolean equals(Object o) {
		Player p = (Player) o;
		return (this.username.equals(p.username) && this.index == p.index);
	}
	
	public String getUsername() {
		return username;
	}
}
