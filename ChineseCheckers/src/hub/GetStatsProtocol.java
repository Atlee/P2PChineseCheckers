package hub;

import java.io.IOException;
import java.net.Socket;
import java.security.Key;

import utils.Constants;
import utils.NetworkUtils;

public class GetStatsProtocol implements HubProtocol {

	@Override
	public void execute(Socket s, Key sharedKey) {
		try {
			String host = new String(NetworkUtils.readEncryptedMessage(s, sharedKey, Constants.SHARED_ENCRYPT_ALG));
			String playerLog = new String(NetworkUtils.readEncryptedMessage(s, sharedKey, Constants.SHARED_ENCRYPT_ALG));
			
			GameDescription gd = Hub.getGameDescription(host);
			String playername = Hub.getUser(s.getInetAddress()).getUsername();
			
			gd.addLog(playername, playerLog);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
