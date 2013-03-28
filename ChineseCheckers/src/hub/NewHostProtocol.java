package hub;

import java.net.InetAddress;
import java.net.Socket;
import java.security.Key;

public class NewHostProtocol implements HubProtocol {

	@Override
	public void execute(Socket s, Key sharedKey) {
		//TODO: add a success/failure response
		InetAddress addr = s.getInetAddress();
		User u = Hub.getUser(addr);
		
		Hub.addUserHost(u);
	}

}
