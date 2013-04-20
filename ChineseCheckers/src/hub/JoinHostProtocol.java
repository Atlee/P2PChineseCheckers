package hub;

import java.io.IOException;
import java.net.Socket;
import java.security.Key;
import java.util.HashMap;

import utils.Constants;
import utils.NetworkUtils;

public class JoinHostProtocol implements HubProtocol {

	@Override
	public void execute(Socket s, Key sharedKey) {
		try {
			String hostname = new String(NetworkUtils.readEncryptedMessage(s, sharedKey, Constants.SHARED_ENCRYPT_ALG));
			HashMap<String, User> hosts = Hub.getUserHost();
			NetworkUtils.sendEncryptedMessage(s, hosts.get(hostname).getAddr().getAddress(), sharedKey, Constants.SHARED_ENCRYPT_ALG);
			hosts.remove(hostname);
		} catch (IOException e) {
			
		}
		
	}

}
