package hub;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.List;

import javax.net.ssl.SSLSocket;

import utils.Constants;

public class ReadyHandler extends HubHandler {

	public ReadyHandler(MultiThreadedHub hub, SSLSocket client,
			ObjectInputStream in) throws IOException {
		super(hub, client, in);
	}
	
	public void run() {
		try {
			String uname = (String) in.readObject();
			if (checkCredentials(uname)) {
				Integer gameID = (Integer) in.readObject();
				
				//blocks until all players have called
				GameKeys gk = hub.games.playerReady(gameID, uname);
				List<String> players = hub.games.getPlayers(gameID);
				if (gk != null && players != null) {
					out.writeObject(players.size());
					out.writeObject(gk.encryptKey);
					
					for (String player : players) {
						out.writeObject(player);
						out.writeObject(gk.signKeys.get(player));
						out.writeObject(hub.online.getInetAddr(player));
					}
				} else {
					out.writeObject(0);
				}
			} else {
				out.writeObject(Constants.INVALID_SECRET);
			}
		} catch (IOException | ClassNotFoundException e) {
			
		}
	}

}
