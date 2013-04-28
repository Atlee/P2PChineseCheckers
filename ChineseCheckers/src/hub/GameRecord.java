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
	
	// Ordered list of usernames of all players in this game
	List<String> players = new ArrayList<String>();
	// Map: {username -> PlayerRecord} for all players in this game
	Map<String, PlayerRecord> playerRecords = new HashMap<String, PlayerRecord>();
	
	// List of players that have clicked ready
	List<String> ready = new ArrayList<String>();
	
	public GameRecord( int gameID, String gameName, int numPlayers, String hostName, 
			int hostSessionID, PublicKey hostKey ) {
		this.gameID = gameID;
		this.gameName = gameName;
		this.numPlayers = numPlayers;
		
		this.players.add(hostName);
		this.playerRecords.put(hostName, new PlayerRecord(hostName, hostSessionID, hostKey));
		
		gameEncryptKey = EncryptUtils.handleCreateSharedKey();
	}
}

class PlayerRecord {
	
	final String username;
	final int sessionID;
	final PublicKey signKey;
	
	String log = null;
	
	public PlayerRecord( String username, int sessionID, PublicKey signKey ) {
		this.username = username;
		this.sessionID = sessionID;
		this.signKey = signKey;
	}
	
}