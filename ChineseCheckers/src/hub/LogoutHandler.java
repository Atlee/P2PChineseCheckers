package hub;

import java.io.IOException;
import java.io.ObjectInputStream;

import javax.net.ssl.SSLSocket;

public class LogoutHandler extends HubHandler {

	public LogoutHandler(MultiThreadedHub hub, SSLSocket client,
			ObjectInputStream in) throws IOException {
		super(hub, client, in);
	}
	
	@Override
	public void run() {
		try {
			String uname = (String) in.readObject();
			if (checkCredentials(uname)) {
				Integer gameID = hub.online.getInGame(uname);
				if(gameID != null) {
					hub.games.leaveGame(gameID, uname);
				}
				hub.online.remove(uname);
			}
		} catch (IOException | ClassNotFoundException ex) {
			;
		}
	}
}
