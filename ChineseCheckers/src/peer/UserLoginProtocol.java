package peer;

import java.io.IOException;
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
		
		try {
			NetworkUtils.sendEncryptedMessage(s, username.getBytes(), sharedKey, Constants.SHARED_ENCRYPT_ALG);
			NetworkUtils.sendEncryptedMessage(s, NetworkUtils.charsToBytes(password), sharedKey, Constants.SHARED_ENCRYPT_ALG);
		} catch (IOException e) {
			System.out.println("Error sending credentials to hub");
			System.exit(1);
		}
	}
	
	public boolean isAuthenticated(Socket s, Key sharedKey) throws IOException {
		String response = new String(NetworkUtils.readEncryptedMessage(s, sharedKey, Constants.SHARED_ENCRYPT_ALG));
		if (response.equals(Constants.LOGIN_SUCCESS)) {
			return true;
		} else {
			return false;
		}
	}
	
	public boolean login(Socket s, Key sharedKey, String username, char[] password) throws IOException {
		sendSharedKey(s, sharedKey);
		NetworkUtils.sendProtocolID(s, Constants.LOGIN);		
		boolean output = false;
		
		NetworkUtils.sendEncryptedMessage(s, username.getBytes(), sharedKey, Constants.SHARED_ENCRYPT_ALG);
		NetworkUtils.sendEncryptedMessage(s, NetworkUtils.charsToBytes(password), sharedKey, Constants.SHARED_ENCRYPT_ALG);
		
		String response = new String(NetworkUtils.readEncryptedMessage(s, sharedKey, Constants.SHARED_ENCRYPT_ALG));
		if (response.equals(Constants.LOGIN_SUCCESS)) {
			output = true;
		}
		return output;
	}
	
	private void sendSharedKey(Socket s, Key sharedKey) {
		PublicKey hubPublic = Constants.getHubPublicKey();
		
		try {
			NetworkUtils.sendEncryptedMessage(s, sharedKey.getEncoded(), hubPublic, Constants.PUBLIC_ENCRYPT_ALG);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
}
