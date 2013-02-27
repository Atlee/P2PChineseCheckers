package peer;

import java.io.IOException;

import java.net.InetAddress;
import java.net.Socket;

import java.security.KeyStore;

import utils.Constants;
import utils.MyKeyStore;
import utils.Protocol;


public class Peer {

	private static InetAddress host;

	public static void main(String[] args) throws IOException {
		MyKeyStore keyStore = new MyKeyStore();
		Socket peer = handleCreateSocket();
    	
    	Protocol p = setProtocol("register", keyStore);
    	p.execute(peer);
    	
		//MyGui gui = new MyGui();
		
	}
	
	public static Protocol setProtocol(String ID, MyKeyStore ks) {
		Protocol p = null;
		if (ID.equals("register")) {
			p = new UserRegistrationProtocol(ks);
		} else if (ID.equals("login")) {
            p = new UserLoginProtocol(ks);
        } else {
        	System.out.println("Unrecognized protocol ID");
        	System.exit(1);
		}
		return p;
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
