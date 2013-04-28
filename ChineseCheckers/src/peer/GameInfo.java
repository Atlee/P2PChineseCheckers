package peer;

import java.net.InetAddress;
import java.security.Key;
import java.security.PublicKey;
import java.util.List;
import java.util.Map;

public class GameInfo {
	final int gameID;		  // unique identifier for this game
	final Key encryptKey; // shared encryption key for this game
	
	//the usernames of all the players in this game
	final List<String> players;
	// Map: {username -> public key}, contains a mapping for key u iff u in 'players'
	final Map<String, PublicKey> playerKeys;
	final Map<String, InetAddress> playerAddrs;
	
	public GameInfo( int gameID, Key encryptKey, Map<String, PublicKey> signKeys, 
			Map<String, InetAddress> playerAddrs, List<String> players ) {
		this.gameID = gameID;	
		this.encryptKey = encryptKey;
		this.playerKeys = signKeys;
		this.playerAddrs = playerAddrs;
		this.players = players;
	}
}
