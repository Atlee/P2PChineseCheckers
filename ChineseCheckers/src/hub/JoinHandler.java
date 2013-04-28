package hub;


import java.io.IOException;
import java.io.ObjectInputStream;
import java.security.PublicKey;
import java.util.UUID;

import javax.net.ssl.SSLSocket;

public class JoinHandler extends HubHandler {

	public JoinHandler(MultiThreadedHub hub, SSLSocket client,
			ObjectInputStream in) throws IOException {
		super(hub, client, in);
	}
	
	@Override
	public void run() {
		try {
			String uname = (String) in.readObject();
			if (checkCredentials(uname)) {
				Integer id = (Integer) in.readObject();
				PublicKey signKey = (PublicKey) in.readObject();
				hub.games.joinGame(id, uname, signKey);
				hub.online.setInGame(uname, id);
			}	
		} catch (IOException | ClassNotFoundException e) {
			;
		}
	}
}
