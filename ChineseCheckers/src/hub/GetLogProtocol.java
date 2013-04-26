package hub;

import java.io.IOException;
import java.net.Socket;
import java.security.Key;

import javax.net.ssl.SSLSocket;

import utils.Constants;
import utils.NetworkUtils;

public class GetLogProtocol extends HubProtocol {

	public GetLogProtocol(SSLSocket client) throws IOException {
		super(client);
	}

	@Override
	public void run() {
		try {
			String host = new String(NetworkUtils.readEncryptedMessage(s, sharedKey, Constants.SHARED_ENCRYPT_ALG));
			String playerLog = new String(NetworkUtils.readEncryptedMessage(s, sharedKey, Constants.SHARED_ENCRYPT_ALG));
			
			GameDescription gd = Hub.getGameDescription(host);
			String playername = Hub.getUser(s.getInetAddress()).getUsername();
			
			gd.addLog(playername, playerLog);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
}
