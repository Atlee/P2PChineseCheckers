package hub;

import java.io.DataInputStream;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyStore;
import java.security.PrivateKey;

import utils.Constants;
import utils.KeyStoreUtils;
import utils.Protocol;


public class Hub {

	public static KeyStore keyStore;

	public static void main(String[] args) throws IOException {
		if(keyStore == null) {
			keyStore = KeyStoreUtils.loadKeyStore(Constants.KEYSTORE_FILE);

			ObjectInputStream in = new ObjectInputStream((new FileInputStream("private.key")));
			try {
				PrivateKey key = (PrivateKey) in.readObject();
				in.close();
				KeyStoreUtils.addPrivateKey(keyStore, key, null, "hub", "password");
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		ServerSocket hub = handleCreateServerSocket();
		
		while (true) {
			// wait for a peer to connect
			Socket peer = handleCreateSocket(hub);
			Protocol p = selectProtocol(peer);
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
	
	private static Protocol selectProtocol(Socket s) {
		DataInputStream in = null;
		int id = -1;
		Protocol p = null;
		
		try {
			in = new DataInputStream(s.getInputStream());
			id = in.readInt();
		} catch (IOException e) {
			System.out.println("Error determining protocol ID");
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
			System.out.println("Unrecognized protocol ID");
			return null;
		}
		return p;
	}
	
	private static ServerSocket handleCreateServerSocket() {
		// start Hub listening on port 4321
		ServerSocket hub = null;
		try {
			hub = new ServerSocket(Constants.PORT_NUM);
		} catch (IOException e) {
			System.out.println("Could not listen on port " + Constants.PORT_NUM);
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
			System.out.println("Accept failed:" + Constants.PORT_NUM);
			e.printStackTrace();
			System.exit(-1);
		}
		return peer;
	}
}
