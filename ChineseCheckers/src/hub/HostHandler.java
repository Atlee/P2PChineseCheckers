package hub;


import java.io.IOException;
import java.io.ObjectInputStream;
import java.security.PublicKey;
import java.util.Set;
import java.util.UUID;

import javax.net.ssl.SSLSocket;

public class HostHandler extends HubHandler {

	public HostHandler(MultiThreadedHub hub, SSLSocket client,
			ObjectInputStream in) throws IOException {
		super(hub, client, in);
	}
	
	@Override
	public void run () {
		try {
			String uname      = in.readUTF(); 
			if (checkCredentials(uname)) {
				
				String gameName   = in.readUTF();
				int numPlayers    = in.readInt();
				PublicKey hostKey = (PublicKey) in.readObject();
				
				UUID gameID = hub.games.createGame(gameName, numPlayers, uname, hostKey);
				
				//blocks and waits for all players to join
				GameKeys gk = hub.games.playerReady(gameID, uname);
				
				//write the game encrypt key
				out.writeObject(gk.encryptKey);
				
				//write the signing keys of each player
				out.writeInt(gk.signKeys.size());
				Set<String> keys = gk.signKeys.keySet();
				for (String player : keys) {
					out.writeUTF(player);
					out.writeObject(gk.signKeys.get(player));
				}
			}			
		} catch (IOException | ClassNotFoundException e) {
			;
		}		
	}

}
