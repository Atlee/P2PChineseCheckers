package peer;

import java.net.Socket;
import java.security.Key;
import java.security.PublicKey;

import utils.Constants;
import utils.EncryptUtils;
import utils.NetworkUtils;
import utils.Protocol;

public class UserLoginProtocol extends Protocol {
	
	public UserLoginProtocol() {
		
	}
	
	public void sendCredentials(Socket s, Key sharedKey, String username, char[] password) {
		sendSharedKey(s, sharedKey);
		NetworkUtils.sendProtocolID(s, Constants.LOGIN);		
				
		NetworkUtils.sendEncryptedMessage(s, username.getBytes(), sharedKey, Constants.SHARED_ENCRYPT_ALG);
		NetworkUtils.sendEncryptedMessage(s, NetworkUtils.charsToBytes(password), sharedKey, Constants.SHARED_ENCRYPT_ALG);		
	}
	
	public boolean isAuthenticated(Socket s, Key sharedKey) {
		String response = new String(NetworkUtils.readEncryptedMessage(s, sharedKey, Constants.SHARED_ENCRYPT_ALG));
		if (response.equals(Constants.LOGIN_SUCCESS)) {
			return true;
		} else {
			return false;
		}
	}
	
	private void sendSharedKey(Socket s, Key sharedKey) {
		PublicKey hubPublic = Constants.getHubPublicKey();
		
		NetworkUtils.sendEncryptedMessage(s, sharedKey.getEncoded(), hubPublic, Constants.PUBLIC_ENCRYPT_ALG);
		System.out.println("Sending encrypted shared key");
	}
}
