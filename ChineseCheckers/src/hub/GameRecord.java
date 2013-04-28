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
	
	// The usernames of all players that have joined (and not yet left) this game
	List<String> players = new ArrayList<String>();
	// Map: {username -> public key}, contains a mapping for key u iff u in 'players'
	Map<String, PublicKey> playerKeys = new HashMap<String, PublicKey>();
	// Map: {username -> audit log}, contains a mapping for key u iff u in 'players'
	Map<String, String> playerLogs = new HashMap<String, String>();
	
	List<String> ready = new ArrayList<String>();
	
	public GameRecord( int gameID, String gameName, int numPlayers, String hostName, PublicKey hostKey ) {
		this.gameID = gameID;
		this.gameName = gameName;
		this.numPlayers = numPlayers;
		
		this.players.add(hostName);
		this.playerKeys.put(hostName, hostKey);
		this.playerLogs.put(hostName, null);
		
		gameEncryptKey = EncryptUtils.handleCreateSharedKey();
	}

}
