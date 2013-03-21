package hub;

import java.net.Socket;
import java.security.Key;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.util.UUID;

import utils.Constants;
import utils.NetworkUtils;
import utils.Protocol;

public class UserLoginProtocol extends Protocol implements HubProtocol {

	@Override
	public void execute(Socket s, Key sharedKey) {
		PasswordStore pws = new PasswordStore();
		String username = new String(NetworkUtils.readEncryptedMessage(s, sharedKey, Constants.SHARED_KEY_ALGORITHM));
		
		String sessionID = UUID.randomUUID().toString();
		NetworkUtils.sendSignedMessage(s, sessionID.getBytes(), HubConstants.getHubPrivate());
		
		String userResponse = "";//new String(NetworkUtils.readSignedMessage(s, userPublic));
		byte[] lastFromHub = null;
		if (userResponse.equals(sessionID)) {
			lastFromHub = "WELCOME".getBytes(); 
		} else {
			lastFromHub = "FAILURE".getBytes();
		}
		NetworkUtils.sendSignedMessage(s,lastFromHub, HubConstants.getHubPrivate());
	}
}
