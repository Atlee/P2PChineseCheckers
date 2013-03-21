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
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.SecretKeySpec;

import utils.Constants;
import utils.EncryptUtils;
import utils.NetworkUtils;
import utils.Protocol;


public class Hub {

	public static void main(String[] args) throws IOException {		
		ServerSocket hub = handleCreateServerSocket();
		
		while (true) {
			// wait for a peer to connect
			Socket peer = handleCreateSocket(hub);
			Key sharedKey = handleGetSharedKey(peer);
			System.out.println("Success");
			HubProtocol p = selectProtocol(peer);
			if(p != null) {
				p.execute(peer, sharedKey);
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
			System.out.println("Error determining protocol ID");
			e.printStackTrace();
			System.exit(1);
		}

		System.out.println(id);
		switch (id) {
		case Constants.REGISTER:
			p = new UserRegistrationProtocol();
			break;
		case Constants.LOGIN:
			p = new UserLoginProtocol();
			break;
		default:
			System.out.println("Unrecognized protocol ID");
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
	
	private static Key handleGetSharedKey(Socket s) {
		try {
			PrivateKey hubPrivate = HubConstants.getHubPrivate();
			byte[] sharedKeyBytes = NetworkUtils.readEncryptedMessage(s, hubPrivate, Constants.PUBLIC_ENCRYPT_ALG);
			
			SecretKeyFactory skf = SecretKeyFactory.getInstance(Constants.SHARED_KEY_ALGORITHM);
			DESKeySpec keySpec = new DESKeySpec(sharedKeyBytes);
			return skf.generateSecret(keySpec);
		} catch (InvalidKeySpecException | NoSuchAlgorithmException | InvalidKeyException e) {
			e.printStackTrace();
			System.exit(1);
		}
		return null;
	}
}
