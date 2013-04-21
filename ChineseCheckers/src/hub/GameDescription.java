package hub;

import java.security.Key;
import java.util.HashMap;

import utils.Constants;
import utils.EncryptUtils;
import utils.NetworkUtils;

public class GameDescription {
	private String hostname;
	//mapping from username to their log
	private HashMap<String, String> players;
	private String winner;
	private Key k;

	public GameDescription(String host) {
		hostname = host;
		players = new HashMap<String, String>();
		players.put(host, null);
		k = EncryptUtils.handleCreateSharedKey();
		winner = null;
	}
	
	public void addPlayer(String player) {
		players.put(player, null);
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

	public void addLog(String playername, String playerLog) {
		players.put(playername, playerLog);		
	}
}
