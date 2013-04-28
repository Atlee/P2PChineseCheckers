package hub;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Set;

import javax.net.ssl.SSLSocket;


public class GetGamePlayersHandler extends HubHandler {

	public GetGamePlayersHandler(MultiThreadedHub hub, SSLSocket client,
			ObjectInputStream in) throws IOException {
		super(hub, client, in);
	}
	
	public void run() {
		try {
			String uname = (String) in.readObject();
			if (checkCredentials(uname)) {
				Integer id = (Integer) in.readObject();
				Set<String> players = hub.games.getPlayers(id).keySet();
				if (players != null) {
					out.writeObject(players.size());
					for (String pname : players) {
						out.writeObject(pname);
					}
				} else {
					out.writeObject(0);
				}
			}
		} catch (IOException | ClassNotFoundException e) {
			;
		}
	}

}
