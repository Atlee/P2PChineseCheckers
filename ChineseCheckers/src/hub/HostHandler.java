package hub;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.security.PublicKey;

import javax.net.ssl.SSLSocket;


public class HostHandler extends HubHandler {

	public HostHandler(MultiThreadedHub hub, SSLSocket client,
			ObjectInputStream in) throws IOException {
		super(hub, client, in);
	}
	
	@Override
	public void run () {
		try {
			String uname = (String) in.readObject(); 
			if (checkCredentials(uname)) {
				String gameName   = (String) in.readObject();
				int numPlayers    = (Integer) in.readObject();
				PublicKey hostKey = (PublicKey) in.readObject();
				
				Integer sessionID = hub.online.getSessionID(uname);
				Integer gameID = hub.games.createGame(gameName, numPlayers, uname, sessionID, hostKey);
				
				out.writeObject(gameID);
				
				hub.online.setInGame(uname, gameID);
			}
		} catch (IOException | ClassNotFoundException e) {
			;
		}		
	}

}
