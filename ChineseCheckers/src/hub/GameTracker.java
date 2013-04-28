package hub;

import java.security.Key;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


/* Monitor instantiated by the multi-threaded hub to track status of
 * active games in the system.
 * @author Emma
 */
public class GameTracker {

	private int nextID = 0;

	// Invariant: Iff ID x is in 'activeGames', then there is EXACTLY ONE active
	// GameRecord with ID x and it is included in EXACTLY ONE of the sets 'joinable'
	// or 'inProgress'.

	// The IDs for all currently active games
	private List<Integer> activeGames = new ArrayList<Integer>();
	// Map: {ID -> GameRecord}
	private Map<Integer, GameRecord> joinable = new HashMap<Integer, GameRecord>();
	private Map<Integer, GameRecord> inProgress = new HashMap<Integer, GameRecord>();

	/* Create a new game with the specified name, hosted by user 'hostName' with session ID
	 * 'hostSessionID' and public key 'hostKey'. Return a unique identifier for this game.
	 * Note: numPlayers is currently ignored and always set to 2... xD
	 */
	synchronized int createGame(String gameName, int numPlayers, String hostName,
			int hostSessionID, PublicKey hostKey) {
		GameRecord record = new GameRecord(nextID++, gameName, 2, hostName, hostSessionID, hostKey);
		activeGames.add(record.gameID);
		joinable.put(record.gameID, record);
		return record.gameID;
	}

	/* Kill a game (i.e. remove it from the set of active games) at any time. */
	synchronized void killGame(int gameID) {
		activeGames.remove(activeGames.indexOf(gameID));
		// try this, in case the game was in the join phase
		joinable.remove(gameID);
		// try this, in case the game was in progress
		inProgress.remove(gameID);
	}

	/* Add a player with public key 'playerKey' to the specified game. This will only work
	 * if the game exists, is currently 'joinable', and is not already full. Return true
	 * iff the player was successfully added. */
	synchronized boolean joinGame(int gameID, String playerName, int playerSessionID, 
			PublicKey playerKey) {
		boolean success = false;
		if(joinable.containsKey(gameID)) {
			GameRecord record = joinable.get(gameID);
			if(record.players.size() < record.numPlayers) {
				record.players.put(playerName, playerSessionID);
				record.playerKeys.put(playerName, playerKey);
				record.playerLogs.put(playerName, null);
				success = true;
			}
		}
		return success;
	}

	/* Remove the specified player from the specified game. This will only work if the
	 * game exists and is currently 'joinable', not 'inProgress'. Also, if 'playerName' is
	 * not actually a player in this game, then do nothing.
	 */
	synchronized void leaveGame(int gameID, String playerName) {
		GameRecord record;
		if(joinable.containsKey(gameID)) {
			record = joinable.get(gameID);
		} else if(inProgress.containsKey(gameID)) {
			record = inProgress.get(gameID);
		} else {
			return;
		}
		record.players.remove(playerName);
		record.ready.remove(playerName);
		record.playerKeys.remove(playerName);
		record.playerLogs.remove(playerName);
		if(record.players.size() < 1) {
			activeGames.remove(activeGames.indexOf(gameID));
			joinable.remove(gameID);
		}
	}


	/* TODO: write comment */
	synchronized Map<String, Integer> getPlayers(int gameID) {
		if(joinable.containsKey(gameID)) {
			return joinable.get(gameID).players;
		} else if(inProgress.containsKey(gameID)) {
			return inProgress.get(gameID).players;
		}
		return null;
	}

	/* TODO: write comment */
	synchronized GameKeys playerReady(int gameID, String playerName) {
		GameKeys keys = null;
		if(joinable.containsKey(gameID)) {
			GameRecord record = joinable.get(gameID);
			if(record.players.containsKey(playerName)) {
				record.ready.remove(playerName);
				record.ready.add(playerName);
				if(record.ready.size() == record.numPlayers) {
					joinable.remove(gameID);
					inProgress.put(gameID, record);
					notifyAll();
				} else {
					while(record.ready.size() < record.numPlayers) {
						try {
							wait();
						} catch(InterruptedException e) {
							record.ready.remove(playerName);
							System.out.println("lolwat 1");
							return null;
						}
						if(!activeGames.contains(gameID)) {
							System.out.println("lolwat 2");
							return null;
						}
						if(inProgress.containsKey(gameID)) {
							record = inProgress.get(gameID);
						} else {
							record = joinable.get(gameID);
						}
						if(!record.players.containsKey(playerName)) {
							System.out.println("lolwat 3");
							return null;
						}
					}
				}
				Key encryptKey = record.gameEncryptKey;
				Map<String, PublicKey> signKeys = record.playerKeys;
				keys = new GameKeys(encryptKey, signKeys);
			}
		}
		return keys;
	}

	/* TODO: write comment */
	synchronized GameRecord submitLog(int gameID, String playerName, String log) {
		GameRecord completeRecord = null;
		if(inProgress.containsKey(gameID)) {
			GameRecord record = inProgress.get(gameID);
			if(record.players.containsKey(playerName)) {
				record.playerLogs.remove(playerName);
				record.playerLogs.put(playerName, log);
				if(!record.playerLogs.values().contains(null)) {
					completeRecord = record;
					activeGames.remove(gameID);
					inProgress.remove(gameID);
				}
			}
		}
		return completeRecord;
	}

	/* TODO: write comment */
	synchronized Map<Integer, String> allJoinableGames() {
		Map<Integer, String> joinableGames = new HashMap<Integer, String>();
		for(int id : joinable.keySet()) {
			GameRecord record = joinable.get(id);
			joinableGames.put(id, record.gameName);
		}
		return joinableGames;
	}
	
	/* TODO: write comment */
	synchronized Set<Integer> allInProgressGames() {
	    return inProgress.keySet();
	}

}
