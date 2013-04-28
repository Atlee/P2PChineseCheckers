package hub;

import java.security.Key;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import utils.EncryptUtils;


/* The GameTracker monitor instantiates these to track individual active games.
 * @author Emma
 */
public class GameRecord {
	
	final int gameID;		  // unique identifier for this game
	final String gameName;    // name of this game
	final int numPlayers;     // total number of players required to start this game
	final Key gameEncryptKey; // shared encryption key for this game
	
	// Map: {username -> session ID} for all players in this game
	Map<String, Integer> players = new HashMap<String, Integer>();
	// Map: {username -> public key}, contains a mapping for key u iff u in players.keySet()
	Map<String, PublicKey> playerKeys = new HashMap<String, PublicKey>();
	// Map: {username -> audit log}, contains a mapping for key u iff u in players.keySet()
	Map<String, String> playerLogs = new HashMap<String, String>();
	
	// List of players that have clicked ready
	List<String> ready = new ArrayList<String>();
	
	public GameRecord( int gameID, String gameName, int numPlayers, String hostName, 
			int hostSessionID, PublicKey hostKey ) {
		this.gameID = gameID;
		this.gameName = gameName;
		this.numPlayers = numPlayers;
		
		this.players.put(hostName, hostSessionID);
		this.playerKeys.put(hostName, hostKey);
		this.playerLogs.put(hostName, null);
		
		gameEncryptKey = EncryptUtils.handleCreateSharedKey();
	}

}
