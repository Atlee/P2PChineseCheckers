package hub;

import java.security.PublicKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;


/* The GameTracker monitor instantiates these to track individual active games.
 * @author Emma
 */
public class GameRecord {
	
	final String hostName;   // username of the host (creator) of this game
	final String gameName;   // name of this game
	final UUID gameID;		 // unique identifier for this game
	final int numPlayers;    // total number of players required to start this game
	//final PublicKey gameKey; // shared encryption key for this game
	
	// The usernames of all players that have joined (and not yet left) this game
	List<String> players = new ArrayList<String>();
	// Map: {username -> public key}, contains a mapping for key u iff u in 'players'
	Map<String, PublicKey> playerKeys = new HashMap<String, PublicKey>();
	
	// TODO: what is the type of a player audit logs?
	// Map: {username -> audit log}, contains a mapping for key u iff u in 'players'
	// Map<String, ???> playerLogs = new HashMap<String, ???>();
	
	public GameRecord( String gameName, int numPlayers, String hostName, PublicKey hostKey ) {
		this.gameName = gameName;
		this.hostName = hostName;
		this.gameID = UUID.randomUUID();
		this.numPlayers = numPlayers;
		
		this.players.add(this.hostName);
		this.playerKeys.put(this.hostName, hostKey);
		
		//TODO generate a shared encryption key for this game
	}

}
