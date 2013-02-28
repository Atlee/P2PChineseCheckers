package hub;

import java.net.Socket;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.util.UUID;

import utils.MyKeyStore;
import utils.NetworkUtils;
import utils.Protocol;

public class UserLoginProtocol extends Protocol implements HubProtocol {
	
	private MyKeyStore ks;

	public UserLoginProtocol(MyKeyStore ks) {
		this.ks = ks;
	}

	@Override
	public void execute(Socket s) {
		String username = new String(NetworkUtils.readMessage(s));
		PublicKey userPublic = ks.getPublicKey(username);
		PrivateKey hubPrivate = ks.getPrivateKey("hub", "password".toCharArray());
		
		String sessionID = UUID.randomUUID().toString();
		NetworkUtils.sendSignedMessage(s, sessionID.getBytes(), hubPrivate);
		
		String userResponse = new String(NetworkUtils.readSignedMessage(s, userPublic));
		byte[] lastFromHub = null;
		if (userResponse.equals(sessionID)) {
			lastFromHub = "WELCOME".getBytes(); 
		} else {
			lastFromHub = "FAILURE".getBytes();
		}
		NetworkUtils.sendSignedMessage(s,lastFromHub, hubPrivate);
	}
}
