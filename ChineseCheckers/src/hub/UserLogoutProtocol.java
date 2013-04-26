package hub;

import java.io.IOException;
import java.io.ObjectInputStream;

import javax.net.ssl.SSLSocket;

public class UserLogoutProtocol extends HubProtocol {

	public UserLogoutProtocol(SSLSocket client, ObjectInputStream in) throws IOException {
		super(client, in);
	}

	@Override
	public void run() {
		if (verifySession()) {
			Hub.logoutUser(username);
		}
	}
}
