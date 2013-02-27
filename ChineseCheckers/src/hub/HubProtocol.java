package hub;

import java.net.Socket;

public interface HubProtocol {
	public void execute(Socket s);
}
