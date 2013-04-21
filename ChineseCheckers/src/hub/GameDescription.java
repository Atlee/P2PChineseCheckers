package hub;

import java.security.Key;
import java.util.HashMap;
import java.util.Set;

import utils.Constants;
import utils.EncryptUtils;
import utils.NetworkUtils;

public class GameDescription {
	private String hostname;
	//mapping from username to their log
	private HashMap<String, String> players;
	private int logs = 0;
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
	
	public Set<String> getPlayers() {
		return players.keySet();
	}
	
	public String getLog(String player) {
		return players.get(player);
	}

	public synchronized void addLog(String playername, String playerLog) {
		players.put(playername, playerLog);
		logs++;
		
		//we have collected all logs
		if (logs == players.size()) {
			(new Thread(new Compare(this))).start();
			Hub.removeGameDescription(hostname);
		}
	}
}

class Compare implements Runnable {

	GameDescription gd;
	
	public Compare(GameDescription gd) {
		this.gd = gd;
	}
	
	@Override
	public void run() {
		String prev = null;
		for (String username : gd.getPlayers()) {
			if (prev == null) {
				prev = username;
			} else {
				String log1 = gd.getLog(prev);
				String log2 = gd.getLog(username);
				if (!log1.equals(log2)) {
					Hub.flagPlayers(prev, username);
				}
			}
		}
	}
	
}
