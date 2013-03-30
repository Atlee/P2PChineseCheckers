package hub;

import java.io.DataInputStream;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
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
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

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

	//a list of all users currently logged in
	private static HashMap<InetAddress, User> loggedInUsers;
	//a list of all users currently hosting games
	private static List<User> hosts;
	
	public static void main(String[] args) throws IOException {		
		ServerSocket hub = handleCreateServerSocket();
		
		while (true) {
			// wait for a peer to connect
			Socket peer = handleCreateSocket(hub);
			Key sharedKey = handleGetSharedKey(peer);
			HubProtocol p = selectProtocol(peer);
			if(p != null) {
				p.execute(peer, sharedKey);
			}
			closeSocket(peer);
		}
	}
	
	public static User getUser(InetAddress i) {
		if (loggedInUsers == null || !loggedInUsers.containsKey(i)) {
			loggedInUsers = new HashMap<InetAddress, User>();
			return null;
		}
		
		return loggedInUsers.get(i);
	}
	
	public static List<User> getUserHost() {
		if (hosts == null) {
			hosts = new LinkedList<User>();
		}
		return hosts;
	}
	
	public static void addUserLogin(User u) {
		if (loggedInUsers == null) {
			loggedInUsers = new HashMap<InetAddress, User>();
		}
		loggedInUsers.put(u.getAddr(), u);
	}
	
	public static void addUserHost(User u) {
		if (hosts == null) {
			hosts = new LinkedList<User>();
		}
		hosts.add(u);
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
		case Constants.GET_HOSTS:
			p = new GetHostsProtocol();
			break;
		case Constants.NEW_HOST:
			p = new NewHostProtocol();
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
			hub = new ServerSocket(Constants.HUB_PORT);
		} catch (IOException e) {
			System.out.println("Could not listen on port " + Constants.HUB_PORT);
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
			System.out.println("Accept failed:" + Constants.HUB_PORT);
			e.printStackTrace();
			System.exit(-1);
		}
		return peer;
	}
	
	private static Key handleGetSharedKey(Socket s) {
		try {
			PrivateKey hubPrivate = HubConstants.getHubPrivate();
			byte[] sharedKeyBytes = NetworkUtils.readEncryptedMessage(s, hubPrivate, Constants.PUBLIC_ENCRYPT_ALG);
			
			SecretKeyFactory skf = SecretKeyFactory.getInstance(Constants.SHARED_ENCRYPT_ALG);
			DESKeySpec keySpec = new DESKeySpec(sharedKeyBytes);
			return skf.generateSecret(keySpec);
		} catch (InvalidKeySpecException | NoSuchAlgorithmException | InvalidKeyException e) {
			e.printStackTrace();
			System.exit(1);
		}
		return null;
	}
}
