package hub;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.List;
import java.util.Set;

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
				Integer gameID = (Integer) in.readObject();
				
				//blocks until all players have called
				GameKeys gk = hub.games.playerReady(gameID, uname);
				List<String> players = hub.games.getPlayerOrder(gameID);
				if (gk != null && players != null && players.size() > 1) {
					out.writeObject(players.size());
					out.writeObject(gk.encryptKey);
					
					for (String player : players) {
						out.writeObject(player);
						out.writeObject(gk.signKeys.get(player));
						byte[] addrBytes = hub.online.getInetAddr(player).getAddress();
						out.writeObject(addrBytes.length);
						System.out.println("wrote length");
						out.write(addrBytes);
						System.out.println("wrote bytes");
						out.flush();
					}
					
					/*if (uname.equals(players.get(0))) {
						hub.games.okayToTalk(gameID, uname);
					} else {
						String done = (String) in.readObject();
						if (done.equals(Constants.CREATE_SOCKET)) {
							hub.games.isListening(gameID, uname);
						} else {
							throw new IOException();
						}
					}*/
				} else {
					out.writeObject(0);
				}
			} else {
				out.writeObject(Constants.INVALID_SECRET);
			}
		} catch (IOException | ClassNotFoundException e) {
			
		}
	}

}
