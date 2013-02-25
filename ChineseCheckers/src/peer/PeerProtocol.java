package peer;

import java.net.Socket;
import java.security.KeyStore;
import utils.Protocol;


public abstract class PeerProtocol extends Protocol {
	
	public abstract void execute(Socket s, KeyStore ks);
	
	
}
