package hub;

import java.net.Socket;
import java.security.Key;

public interface HubProtocol {
	public void execute(Socket s, Key sharedKey);
}
