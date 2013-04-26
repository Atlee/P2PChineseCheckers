package hub;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.SocketException;
import javax.net.ssl.SSLSocket;

import utils.Constants;


public class UserRegistrationProtocol extends HubProtocol {
	
	public UserRegistrationProtocol(SSLSocket client, ObjectInputStream in) throws IOException {
		super(client, in);
	}

	public void run() {
		try {			
			String message;
			PasswordStore pws = new PasswordStore();
			
			System.out.println("Inside User Reg");
			
			String username = null;
			char[] password = null;
			
			username = in.readUTF();
			password = in.readUTF().toCharArray();
			if (Constants.verifyUsername(username) && Constants.verifyPassword(password)) {
				if (!pws.containsEntry(username)) {
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
			
			client.close();
			
		} catch (SocketException e) {
			;
		} catch (IOException e) {
			System.out.println("Error executing UserRegistrationProtocol");
			e.printStackTrace();
		}
	}
}
