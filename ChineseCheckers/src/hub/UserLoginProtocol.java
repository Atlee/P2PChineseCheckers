package hub;

import java.io.IOException;
import java.net.Socket;
import java.security.Key;

import utils.Constants;
import utils.NetworkUtils;

public class UserLoginProtocol implements HubProtocol {

	@Override
	public void execute(Socket s, Key sharedKey) {
		PasswordStore pws = new PasswordStore();
		try {
			String username = new String(NetworkUtils.readEncryptedMessage(s, sharedKey, Constants.SHARED_ENCRYPT_ALG));
			char[] password = NetworkUtils.bytesToChars(NetworkUtils.readEncryptedMessage(s, sharedKey, Constants.SHARED_ENCRYPT_ALG));
			
			byte[] authenticateResponse = null;
			if (pws.authenticate(username, password)) {
				Hub.addUserLogin(new User(s.getInetAddress(), username));
				authenticateResponse = Constants.LOGIN_SUCCESS.getBytes(); 
			} else {
				authenticateResponse = Constants.LOGIN_FAILURE.getBytes();
			}
			NetworkUtils.sendEncryptedMessage(s, authenticateResponse, sharedKey, Constants.SHARED_ENCRYPT_ALG);
		} catch (IOException e) {
			System.out.println("Error reading credentials from user");
			e.printStackTrace();
		}
	}
}
