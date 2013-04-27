package hub;

import java.security.PublicKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;


/* Monitor instantiated by the multi-threaded hub to track status of
 * active games in the system.
 * @author Emma
 */
public class GameTracker {
	
	// Invariant: Iff UUID x is in 'activeGames', then there is EXACTLY ONE active
	// GameRecord with UUID x and it is included in EXACTLY ONE of the sets 'joinable'
	// or 'inProgress'.
	
	// The UUIDs for all currently active games
	private List<UUID> activeGames = new ArrayList<UUID>();
	// Map: {UUID -> GameRecord}
	private Map<UUID, GameRecord> joinable = new HashMap<UUID, GameRecord>();
	private Map<UUID, GameRecord> inProgress = new HashMap<UUID, GameRecord>();
	
	synchronized UUID createGame(String gameName, int numPlayers, String hostName, PublicKey hostKey) {
		//TODO: implement me!
		return null;
	}
	
	synchronized void killGame(UUID gameID) {
		//TODO: implement me!
	}
	
	synchronized boolean addPlayer(UUID gameID, String playerName, PublicKey playerKey) {
		//TODO: implement me!
		return false;
	}
	
	synchronized boolean removePlayer(UUID gameID, String playerName) {
		//TODO: implement me!
		return false;
	}
	
	synchronized GameKeys playerReady(UUID gameID, String playerName) {
		//TODO: implement me!
		return null;
	}
	
	synchronized void submitLog(UUID gameID, String playerName, String log) {
		//TODO: implement me!
	}
	
	synchronized Map<UUID, String> listGames() {
		//TODO: implement me!
		return null;
	}
	
	synchronized void reapDeadGames() {
		//TODO: implement me!
	}
	
}
