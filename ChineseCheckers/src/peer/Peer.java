package peer;

import java.io.IOException;

import java.net.InetAddress;
import java.net.Socket;

import java.security.KeyStore;

import utils.KeyStoreUtils;
import utils.Constants;
import utils.Protocol;


public class Peer {

	private static InetAddress host;
	private static KeyStore keyStore;
	
	private static Protocol p = null;

	public static void main(String[] args) throws IOException {
    	if(keyStore == null) {
			keyStore = KeyStoreUtils.loadKeyStore(Constants.KEYSTORE_FILE);
		}
    	
    	setProtocol("register");
    	executeProtocol();
    	
		//MyGui gui = new MyGui();
		
	}
	
	public static void setProtocol(String ID) {
		if (ID.equals("register")) {
			p = new UserRegistrationProtocol();
		} else if (ID.equals("login")) {
            p = new UserLoginProtocol();
        } else {
        	System.out.println("Unrecognized protocol ID");
        	System.exit(1);
		}
	}
	
	public static void executeProtocol() {
		if (p != null) {
			Socket hub = handleCreateSocket();
			p.execute(hub, keyStore);
		}
	}
	
	private static Socket handleCreateSocket() {
		Socket s = null;		
		try {
			host = InetAddress.getLocalHost();
			s = new Socket(host, Constants.PORT_NUM);
		} catch (IOException e) {
			System.out.println("Error creating socket");
			e.printStackTrace();
			System.exit(1);
		}
		return s;
	}
		

}
