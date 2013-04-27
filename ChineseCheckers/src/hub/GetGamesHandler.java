package hub;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Map;
import java.util.Set;
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
			String uname = in.readUTF();
			if (checkCredentials(uname)) {
				Map<UUID, String> games = hub.games.listGames();
				//tell client how many game pairs to expect
				out.writeInt(games.size());

				Set<UUID> keySet = games.keySet();
				for (UUID id : keySet) {
					out.writeObject(id);
					out.writeUTF(games.get(id));
				}
			}
		} catch (IOException e) {
			;
		}
	}

}
