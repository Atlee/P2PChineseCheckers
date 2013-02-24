package hub;

import java.net.Socket;
import java.security.KeyStore;
import utils.Protocol;


public abstract class HubProtocol extends Protocol {
	
	public abstract void execute(Socket s, KeyStore ks);
	
}
