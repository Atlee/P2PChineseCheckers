package hub;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.net.ssl.SSLSocket;

import utils.Constants;

public class UserLoginProtocol extends HubProtocol {

	public UserLoginProtocol(SSLSocket client) throws IOException {
		super(client);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void run() {
		PasswordStore pws = new PasswordStore();
		try {
			String username = in.readUTF();
			char[] password = in.readUTF().toCharArray();
			
			String authenticateResponse = null;
			if (pws.authenticate(username, password)) {
				//create a session key for this user
				SecureRandom rand = SecureRandom.getInstance(Constants.RANDOM_ALGORITHM);
				rand.setSeed((username + new String(password)).getBytes());
				
				int sessionKey = rand.nextInt();
				out.writeInt(sessionKey);
				
				Hub.loginUser(client.getInetAddress(), username, sessionKey);
				authenticateResponse = Constants.LOGIN_SUCCESS; 
			} else {
				authenticateResponse = Constants.LOGIN_FAILURE;
			}
			out.writeUTF(authenticateResponse);
		} catch (IOException e) {
			System.out.println("Error reading credentials from user");
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}
}
