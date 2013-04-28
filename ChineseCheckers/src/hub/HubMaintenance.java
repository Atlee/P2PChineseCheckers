package hub;

import java.util.Map;
import java.util.Set;


public class HubMaintenance implements Runnable {

	MultiThreadedHub hub;
	
	public HubMaintenance( MultiThreadedHub hub ) {
		this.hub = hub;
	}
	
	@Override
	public void run() {
		while(true) {
			try {
				Thread.sleep(300000); // sleep for 5 minutes
				// First, log out idle users
				hub.online.reapIdleUsers();
				// Now check for dead games
				Map<String, Integer> online = hub.online.allOnlineUsers();
				Set<Integer> games = hub.games.allInProgressGames();
				for(int gameID : games) {
					boolean dead = false;
					Map<String, Integer> players = hub.games.getPlayers(gameID);
					if(players != null) {
						for(String uname : players.keySet()) {
							int currentSessionID = hub.online.getSessionID(uname);
							if(players.get(uname) != currentSessionID) {
								
							}
						}
					}
				}
			} catch (InterruptedException e) {
				continue;
			}
		}
	}

}
