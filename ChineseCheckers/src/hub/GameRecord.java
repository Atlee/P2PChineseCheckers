package hub;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/* 
 * @author Emma
 */
public class GameRecord {
	
	final String gameName;  // name of this game
	final String hostName;  // username of the host (creator) of this game
	final int numPlayers;   // total number of players required to start this game
	
	// The usernames of all players that have successfully joined this game
	List<String> players = new ArrayList<String>();
	// Map: {username -> InetAddress}, contains a mapping for u iff u in 'players'
	Map<String, InetAddress> playerAddrs = new HashMap<String, InetAddress>();
	
	public GameRecord(String gameName, int numPlayers, String hostName, InetAddress hostAddr) {
		this.gameName = gameName;
		this.hostName = hostName;
		this.numPlayers = numPlayers;
		
		this.players.add(this.hostName);
		this.playerAddrs.put(this.hostName, hostAddr);
	}

}
