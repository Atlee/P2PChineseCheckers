package hub;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/* Monitor instantiated by the hub to track status of active games in the system.
 * @author Emma
 */
public class GameTracker {
	
	// The usernames of all hosts of currently active games
	private List<String> activeHosts = new ArrayList<String>();
	
	// Invariant: If there is an active GameRecord for some host H, then it is the only active
	// GameRecord for host H and it is included in EXACTLY one of the following sets...
	private Map<String, GameRecord> joinable = new HashMap<String, GameRecord>();
	private Map<String, GameRecord> inProgress = new HashMap<String, GameRecord>();
	private Map<String, GameRecord> complete = new HashMap<String, GameRecord>();
	
	synchronized void createGame(String gameName, int numPlayers, String hostName, InetAddress hostAddr) {
		//TODO: implement me!
	}
	
	synchronized boolean addPlayer() {
		return false; //TODO: implement me!
	}
	
	synchronized boolean removePlayer() {
		return false; //TODO: implement me!
	}
	
	//TODO: think of the rest of the functions that need to be in here
}
