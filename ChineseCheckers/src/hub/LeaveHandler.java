package hub;

import java.io.IOException;
import java.io.ObjectInputStream;

import javax.net.ssl.SSLSocket;

public class LeaveHandler extends HubHandler {

	public LeaveHandler(MultiThreadedHub hub, SSLSocket client,
			ObjectInputStream in) throws IOException {
		super(hub, client, in);
	}

	public void run() {
		try {
			String uname = (String) in.readObject();
			if (checkCredentials(uname)) {
				Integer gameID = (Integer) in.readObject();
				
				hub.games.leaveGame(gameID, uname);
				hub.online.setInGame(uname, null);
			}
			
		} catch (IOException | ClassNotFoundException e) {
			;
		}
	}
}
