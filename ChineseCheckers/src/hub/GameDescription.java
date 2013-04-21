package hub;

import java.security.Key;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import utils.EncryptUtils;

public class GameDescription {
	private String hostname;
	private List<String> players;
	private String winner;
	private Key k;

	public GameDescription(String host) {
		hostname = host;
		players = new ArrayList<String>();
		players.add(host);
		k = EncryptUtils.handleCreateSharedKey();
		winner = null;
	}
	
	public String getWinner() {
		return winner;
	}
	
	public void setWinner(String username) {
		winner = username;
	}
	
	public String getHost() {
		return hostname;
	}
	
	public Key getKey() {
		return k;
	}
}
