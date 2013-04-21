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
import java.util.UUID;

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
	private static HashMap<String, User> hosts;
	
	private static HashMap<String, GameDescription> gamesByHost = new HashMap<String, GameDescription>();
	
	public static void main(String[] args) {		
		ServerSocket hub = handleCreateServerSocket();
		
		while (true) {
			try {
				// wait for a peer to connect
				Socket peer = handleCreateSocket(hub);
				if (peer != null) {
					Key sharedKey = handleGetSharedKey(peer);
					HubProtocol p = selectProtocol(peer);
					if(p != null) {
						p.execute(peer, sharedKey);
					}
					closeSocket(peer);
				}
			} catch (IOException e) {
				System.out.println("Error processing client protocol");
			}
		}
	}
	
	public static User getUser(InetAddress i) {
		if (loggedInUsers == null || !loggedInUsers.containsKey(i)) {
			loggedInUsers = new HashMap<InetAddress, User>();
			return null;
		}
		
		return loggedInUsers.get(i);
	}
	
	public static HashMap<String, User> getUserHost() {
		if (hosts == null) {
			hosts = new HashMap<String, User>();
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
			hosts = new HashMap<String, User>();
		}
		hosts.put(u.getUsername(), u);
	}
	
	public static void removeUserLogin(InetAddress addr) {
		if (loggedInUsers == null) {
			return;
		}
		loggedInUsers.remove(addr);
	}
	
	private static void closeSocket(Socket s) throws IOException {
			s.getOutputStream().close();
			s.close();
	}
	
	private static HubProtocol selectProtocol(Socket s) throws IOException {
		DataInputStream in = null;
		int id = -1;
		HubProtocol p = null;
		
		in = new DataInputStream(s.getInputStream());
		id = in.readInt();

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
		case Constants.JOIN_GAME:
			p = new JoinHostProtocol();
			break;
		case Constants.LOGOUT:
			p = new UserLogoutProtocol();
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
	
	private static Socket handleCreateSocket(ServerSocket server) throws IOException {
		Socket peer = null;
		// accept a peer connection
		peer = server.accept();
		return peer;
	}
	
	private static Key handleGetSharedKey(Socket s) throws IOException {
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

	public static void addGameDescription(String host, GameDescription gd) {
		gamesByHost.put(host, gd);
	}
	
	public static GameDescription getGameDescription(String host) {
		return gamesByHost.get(host);
	}
}
