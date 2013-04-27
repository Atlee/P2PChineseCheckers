package hub;

import java.security.PublicKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/* Monitor instantiated by the multi-threaded hub to track status of
 * active games in the system.
 * @author Emma
 */
public class GameTracker {
	
	// Invariant: Iff host H is in 'activeHosts', then there is EXACTLY ONE active
	// GameRecord for host H and it is included in EXACTLY ONE of the sets 'joinable' 
	// or 'inProgress'.
	
	// The host usernames for all currently active games
	private List<String> activeHosts = new ArrayList<String>();
	// Map: {host username -> GameRecord}
	private Map<String, GameRecord> joinable = new HashMap<String, GameRecord>();
	private Map<String, GameRecord> inProgress = new HashMap<String, GameRecord>();
	
	synchronized void createGame(String gameName, int numPlayers, String hostName, PublicKey hostKey) {
		//TODO: implement me!
	}
	
	synchronized Map<String, PublicKey> startGame() {
		//TODO: implement me!
		return new HashMap<String, PublicKey>();
	}
	
	synchronized void killGame() {
		//TODO: implement me!
	}
	
	synchronized boolean addPlayer() {
		//TODO: implement me!
		return false;
	}
	
	synchronized boolean removePlayer() {
		//TODO: implement me!
		return false;
	}
	
	synchronized void submitLog() {
		//TODO: implement me!
	}
	
	private void checkLogs() {
		//TODO: implement me!
	}
}
