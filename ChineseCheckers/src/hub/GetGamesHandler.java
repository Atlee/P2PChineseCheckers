package hub;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Map;
import java.util.UUID;

import javax.net.ssl.SSLSocket;

public class GetGamesHandler extends HubHandler {

	public GetGamesHandler(MultiThreadedHub hub, SSLSocket client,
			ObjectInputStream in) throws IOException {
		super(hub, client, in);
	}
	
	@Override
	public void run() {
		try {
			String uname = (String) in.readObject();
			if (checkCredentials(uname)) {
				Map<Integer, String> games = hub.games.allJoinableGames();
				//tell client how many game pairs to expect
				if (games != null) {
					out.writeObject(games.size());

					for (Integer id : games.keySet()) {
						out.writeObject(id);
						out.writeObject(games.get(id));
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
