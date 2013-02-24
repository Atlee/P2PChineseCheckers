package hub;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyStore;

import utils.Constants;
import utils.KeyStoreUtils;


public class Hub {
	
	private static final int PORT_NUM = Constants.PORT_NUM;
	private static KeyStore keyStore;

	public static void main(String[] args) throws IOException {
		if(keyStore == null) {
			keyStore = KeyStoreUtils.loadKeyStore(Constants.KEYSTORE_FILE);
		}
		
		ServerSocket hub = handleCreateServerSocket();
		
		while (true) {
			// wait for a peer to connect
			Socket peer = handleCreateSocket(hub);
			HubProtocol p = selectProtocol(peer);
			if(p != null) {
				p.execute(peer, keyStore);
			}
			closeSocket(peer);
		}
	}
	
	private static void closeSocket(Socket s) {
		try {
			s.getOutputStream().close();
			s.close();
		} catch (IOException e) {
			System.out.println("Error closing socket");
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	private static HubProtocol selectProtocol(Socket s) {
		DataInputStream in = null;
		int id = -1;
		HubProtocol p = null;
		
		try {
			in = new DataInputStream(s.getInputStream());
			id = in.readInt();
		} catch (IOException e) {
			System.out.println("Error determining protocol ID\n");
			e.printStackTrace();
			System.exit(1);
		}

		switch (id) {
		case Constants.REGISTER:
			p = new UserRegistrationProtocol();
			break;
		case Constants.LOGIN:
			p = new UserLoginProtocol();
		default:
			System.out.println("Unrecognized protocol ID\n");
			return null;
		}
		return p;
	}
	
	private static ServerSocket handleCreateServerSocket() {
		// start Hub listening on port 4321
		ServerSocket hub = null;
		try {
			hub = new ServerSocket(PORT_NUM);
		} catch (IOException e) {
			System.out.println("Could not listen on port " + PORT_NUM + "\n");
			e.printStackTrace();
			System.exit(-1);
		}
		return hub;
	}
	
	private static Socket handleCreateSocket(ServerSocket server) {
		Socket peer = null;
		// accept a peer connection
		try {
			peer = server.accept();
		} catch (IOException e) {
			System.out.println("Accept failed:" + PORT_NUM + "\n");
			e.printStackTrace();
			System.exit(-1);
		}
		return peer;
	}
}
