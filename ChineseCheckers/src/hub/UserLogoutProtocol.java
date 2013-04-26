package hub;

import java.net.InetAddress;
import java.net.Socket;
import java.security.Key;

public class UserLogoutProtocol implements HubProtocol {

	@Override
	public void execute(Socket s, Key sharedKey) {
		InetAddress addr = s.getInetAddress();
		Hub.removeUserLogin(addr);
	}
}
