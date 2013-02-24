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
	
	public static void main(String[] args) throws IOException {
		if(keyStore == null) {
			keyStore = KeyStoreUtils.loadKeyStore(Constants.KEYSTORE_FILE);
		}
		
		while (true) {
			PeerProtocol p = selectProtocol();
			Socket hub = handleCreateSocket();
			p.execute(hub, keyStore);
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
	
	private static PeerProtocol selectProtocol() {
		BufferedReader stdin = new BufferedReader( new InputStreamReader(System.in) );
		String userInput = null;
		
		while(true) {
			System.out.println("Type \"login\" for existing user account or \"register\" for new user account");

			try {
				userInput = stdin.readLine();
			} catch (IOException e) {
				System.out.println("Error reading user input");
				System.exit(1);
			}

			if (userInput.equals("register")) {
				return new UserRegistrationProtocol();
			} 
			if(userInput.equals("login")){
				return new UserLoginProtocol();
			}
		}
	}
		

}
