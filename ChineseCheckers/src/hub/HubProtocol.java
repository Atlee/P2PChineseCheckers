package hub;

import java.net.Socket;
import java.security.Key;

public interface HubProtocol {
	//move socket creation and sharedkey creation here
	//and make a class
	public void execute(Socket s, Key sharedKey);
}
