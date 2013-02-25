package peer;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

import java.net.InetAddress;
import java.net.Socket;

import java.security.KeyStore;

import utils.KeyStoreUtils;
import utils.Constants;


public class Peer {

	private static final int PORT_NUM = Constants.PORT_NUM;
	private static InetAddress host;
	private static KeyStore keyStore;
	
	private static PeerProtocol p = null;
	/**
	 * @param args
	 */
	public static void main(String[] args) {
    	if(keyStore == null) {
			keyStore = KeyStoreUtils.loadKeyStore(Constants.KEYSTORE_FILE);
		}
		MyGui g = new MyGui();
		
	}
	
	public static void setPeerProtocol(String protocolID) {
		p = createProtocol(protocolID);
	}
	
	public static void executeProtocol() {
		if (p != null) {
			Socket hub = handleCreateSocket();
			p.execute(hub);
		}
	}
	
	private static Socket handleCreateSocket() {
		Socket s = null;		
		try {
			host = InetAddress.getLocalHost();
			s = new Socket(host, PORT_NUM);
		} catch (IOException e) {
			System.out.println("Error creating socket");
			e.printStackTrace();
			System.exit(1);
		}
		return s;
	}
	
	private static PeerProtocol createProtocol(String protocolID) {
		if (protocolID.equals("register")) {
			p = new UserRegistrationProtocol();
		} else if (protocolID.equals("login"){
            p = new UserLoginProtocol();
        } else {
			p = new PeerProtocol();
		}
	}
		

}
