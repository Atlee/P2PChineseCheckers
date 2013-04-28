package hub;

import java.security.Key;
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

	/* Create a new game with the specified name, hosted by user 'hostName' with public
	 * key 'hostKey'. Return a unique identifier for this game.
	 * Note: numPlayers is currently ignored and always set to 2... xD
	 */
	synchronized UUID createGame(String gameName, int numPlayers, String hostName, PublicKey hostKey) {
		GameRecord record = new GameRecord(gameName, 2, hostName, hostKey);
		return record.gameID;
	}

	/* Kill a game (i.e. remove it from the set of active games) at any time. */
	synchronized void killGame(UUID gameID) {
		activeGames.remove(gameID);
		joinable.remove(gameID);
		inProgress.remove(gameID);
	}

	/* Add a player with public key 'playerKey' to the specified game. This will only work
	 * if the game exists, is currently 'joinable', and is not already full. Return true
	 * iff the player was successfully added. */
	synchronized boolean addPlayer(UUID gameID, String playerName, PublicKey playerKey) {
		boolean success = false;
		if(joinable.containsKey(gameID)) {
			GameRecord record = joinable.get(gameID);
			if(record.players.size() < record.numPlayers) {
				record.players.add(playerName);
				record.playerKeys.put(playerName, playerKey);
				record.playerLogs.put(playerName, null);
			}
		}
		return success;
	}

	/* Remove the specified player from the specified game. This will only work if the
	 * game exists and is currently 'joinable', not 'inProgress'. Also, if 'playerName' is
	 * not actually a player in this game, then do nothing.
	 */
	synchronized void removePlayer(UUID gameID, String playerName) {
		if(joinable.containsKey(gameID)) {
			GameRecord record = joinable.get(gameID);
			record.players.remove(playerName);
			record.playerKeys.remove(playerName);
			record.playerLogs.remove(playerName);
		}
	}

	/* TODO: write comment */
	synchronized GameKeys playerReady(UUID gameID, String playerName) {
		GameKeys keys = null;
		if(joinable.containsKey(gameID)) {
			GameRecord record = joinable.get(gameID);
			if(record.players.contains(playerName)) {
				record.readyCount += 1;
				while(record.readyCount < record.numPlayers) {
					try {
						wait();
					} catch(InterruptedException e) {
						return null;
					}
					if(!joinable.containsKey(gameID)) {
						return null;
					}
					record = joinable.get(gameID);
					if(!record.players.contains(playerName)) {
						return null;
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
	synchronized Map<String, String> submitLog(UUID gameID, String playerName, String log) {
		Map<String, String> allLogs = null;
		if(inProgress.containsKey(gameID)) {
			GameRecord record = inProgress.get(gameID);
			if(record.players.contains(playerName)) {
				record.playerLogs.remove(playerName);
				record.playerLogs.put(playerName, log);
				if(!record.playerLogs.values().contains(null)) {
					allLogs = record.playerLogs;
				}
			}
		}
		return allLogs;
	}

	/* TODO: write comment */
	synchronized Map<UUID, String> listGames() {
		Map<UUID, String> joinableGames = new HashMap<UUID, String>();
		for(UUID id : joinable.keySet()) {
			GameRecord record = joinable.get(id);
			joinableGames.put(id, record.gameName);
		}
		return joinableGames;
	}
	
	/* TODO: write comment */
	synchronized List<String> getPlayers(UUID gameID) {
		List<String> players = null;
		if(joinable.containsKey(gameID)) {
			GameRecord record = joinable.get(gameID);
			players = record.players;
		}
		return players;
	}

	/* TODO: write comment */
	synchronized void reapDeadGames() {
		//TODO: implement me!
	}

}
