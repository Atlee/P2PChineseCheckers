package hub;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.security.Key;

import utils.Constants;
import utils.NetworkUtils;

public class NewHostProtocol implements HubProtocol {

	@Override
	public void execute(Socket s, Key sharedKey) {
		//TODO: add a success/failure response
		InetAddress addr = s.getInetAddress();
		User u = Hub.getUser(addr);
		
		GameDescription gd = new GameDescription(u.getUsername());
		
		Hub.addGameDescription(gd.getHost(), gd);
		try {
			System.out.println("Sending game key");
			NetworkUtils.sendEncryptedMessage(s, gd.getKey().getEncoded(), sharedKey, Constants.SHARED_ENCRYPT_ALG);
		} catch (IOException e) {
			
		}
		
		Hub.addUserHost(u);
	}

}
