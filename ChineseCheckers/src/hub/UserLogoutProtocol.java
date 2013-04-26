package hub;

import java.io.IOException;
import javax.net.ssl.SSLSocket;

public class UserLogoutProtocol extends HubProtocol {

	public UserLogoutProtocol(SSLSocket client) throws IOException {
		super(client);
	}

	@Override
	public void run() {
		if (verifySession()) {
			Hub.logoutUser(username);
		}
	}
}
