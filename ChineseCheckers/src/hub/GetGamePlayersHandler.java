package hub;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.security.PublicKey;
import java.util.List;
import java.util.UUID;

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
				List<String> players = hub.games.getPlayers(id);
				out.writeObject(players.size());
				for (String pname : players) {
					out.writeObject(pname);
				}
			}
		} catch (IOException | ClassNotFoundException e) {
			;
		}
	}

}
