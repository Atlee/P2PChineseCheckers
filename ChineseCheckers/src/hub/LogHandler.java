package hub;

import java.io.IOException;
import java.io.ObjectInputStream;

import javax.net.ssl.SSLSocket;


public class LogHandler extends HubHandler {

	public LogHandler(MultiThreadedHub hub, SSLSocket client, ObjectInputStream in) throws IOException {
		super(hub, client, in);
	}
	
	@Override
	public void run() {
		try {
			String uname = (String) in.readObject();
			if (checkCredentials(uname)) {
				Integer id = (Integer) in.readObject();
				String log = (String) in.readObject();
				
				GameRecord gr = hub.games.submitLog(id, uname, log);
				if (gr != null) {
					processStats(gr);
				}
			}
		} catch (ClassNotFoundException | IOException e) {
			
		}
	}
	
	private void processStats(GameRecord gr) throws IOException {
		String prev = null;
		String winner = null;
		boolean valid = true;
		for (String player : gr.playerRecords.keySet()) {
			String log1winner = getWinner(gr.playerRecords.get(player).log);
			if (prev != null) {
				String log2winner = getWinner(gr.playerRecords.get(prev).log);
				if (!log1winner.equals(log2winner)) {
					//if the logs of 2 consecuritve players do not match then 
					//invalidate this game
					valid = false;
				}
			}
			if (prev == null) {
				//the first time in the loop
				winner = getWinner(gr.playerRecords.get(player).log);
			}
			prev = player;
		}
		
		if (valid) {
			for (String player : gr.playerRecords.keySet()) {
				int[] wins = hub.statStore.getStats(player);
				if (player.equals(winner) && wins != null && wins.length > 0) {
					wins[0]++;
					hub.statStore.addStats(player, wins);
				}
			}
		}
	}

	private String getWinner(String log) {
		String[] split1 = log.split("\n");
		if (split1.length > 0) {
			split1 = split1[0].split("\t");
			if (split1.length > 1) {
				return split1[1];
			}
		}
		return null;
	}

}
