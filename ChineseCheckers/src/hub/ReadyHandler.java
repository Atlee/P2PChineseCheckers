package hub;

import java.io.IOException;
import java.io.ObjectInputStream;

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
				out.writeObject(Constants.VALID_SECRET);
				Integer gameID = (Integer) in.readObject();
				
				//blocks until all players have called
				GameKeys gk = hub.games.playerReady(gameID, uname);
				if (gk != null) {
					out.writeObject(gk.encryptKey);
					out.writeObject(gk.signKeys.size());
				}				
			} else {
				out.writeObject(Constants.INVALID_SECRET);
			}
		} catch (IOException | ClassNotFoundException e) {
			
		}
	}

}
