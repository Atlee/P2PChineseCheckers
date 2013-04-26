package hub;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.security.Key;
import java.security.KeyStore;
import java.security.PublicKey;

import utils.Constants;
import utils.EncryptUtils;
import utils.NetworkUtils;
import utils.Protocol;


public class UserRegistrationProtocol extends Protocol implements HubProtocol {
	
	public void execute(Socket s, Key sharedKey) {
		try {			
			byte[] message;
			byte[] usernameBytes;
			byte[] passwordBytes;
			PasswordStore pws = new PasswordStore();
			
			boolean usernameAvailable = false;
			String username = null;
			char[] password = null;
			
			while(!usernameAvailable) {
				usernameBytes = NetworkUtils.readEncryptedMessage(s, sharedKey, Constants.SHARED_ENCRYPT_ALG);
				passwordBytes = NetworkUtils.readEncryptedMessage(s, sharedKey, Constants.SHARED_ENCRYPT_ALG);
				
				username = new String(usernameBytes);
				password = NetworkUtils.bytesToChars(passwordBytes);
				if (Constants.verifyUsername(username) && Constants.verifyPassword(password)) {
					if (!pws.containsEntry(username)) {
						usernameAvailable = true;
						if (pws.addEntry(username, password)) {
							message = (Constants.REGISTRATION_SUCCESS+username).getBytes();
						} else {
							message = (Constants.REGISTRATION_FAILURE+username).getBytes();
						}
					} else {
						message = (Constants.REGISTRATION_IN_USE+username).getBytes();
					}
				} else {
					message = (Constants.REGISTRATION_FAILURE+username).getBytes();
				}
				
				NetworkUtils.sendEncryptedMessage(s, message, sharedKey, Constants.SHARED_ENCRYPT_ALG);
			}
			
		} catch (SocketException e) {
			return;
		} catch (IOException e) {
			System.out.println("Error executing UserRegistrationProtocol");
			e.printStackTrace();
			System.exit(1);
		}
	}
}
