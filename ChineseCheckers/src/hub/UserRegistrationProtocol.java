package hub;

import java.io.IOException;
import java.net.SocketException;
import javax.net.ssl.SSLSocket;

import utils.Constants;


public class UserRegistrationProtocol extends HubProtocol {
	
	public UserRegistrationProtocol(SSLSocket client) throws IOException {
		super(client);
	}

	public void run() {
		try {			
			String message;
			PasswordStore pws = new PasswordStore();
			
			boolean usernameAvailable = false;
			String username = null;
			char[] password = null;
			
			while(!usernameAvailable) {
				username = in.readUTF();
				password = in.readUTF().toCharArray();
				if (Constants.verifyUsername(username) && Constants.verifyPassword(password)) {
					if (!pws.containsEntry(username)) {
						usernameAvailable = true;
						if (pws.addEntry(username, password)) {
							message = (Constants.REGISTRATION_SUCCESS+username);
						} else {
							message = (Constants.REGISTRATION_FAILURE+username);
						}
					} else {
						message = (Constants.REGISTRATION_IN_USE+username);
					}
				} else {
					message = (Constants.REGISTRATION_FAILURE+username);
				}
				
				out.writeUTF(message);
			}
			
		} catch (SocketException e) {
			;
		} catch (IOException e) {
			System.out.println("Error executing UserRegistrationProtocol");
			e.printStackTrace();
		} finally {
			try {
				out.close();
				in.close();
				client.close();
			} catch (IOException ex) {
				;
			}
		}
	}
}
