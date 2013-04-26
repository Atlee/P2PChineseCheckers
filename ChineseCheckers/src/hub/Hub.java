package hub;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.InetAddress;
import java.security.Key;
import java.security.KeyStore;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;

import utils.Constants;


public class Hub {
	
	private final KeyStore keyStore;
	private final char[] ksPassword;
	
	private static HashMap<String, Integer> sessionKeyMap = new HashMap<String, Integer>();
	private static Lock sessionKeyLock = new ReentrantLock();
	private static HashMap<String, InetAddress> addrMap = new HashMap<String, InetAddress>();
	private static Lock addrMapLock = new ReentrantLock();
	//games that have not had their stats collected
	private static HashMap<String, GameDescription> games = new HashMap<String, GameDescription>();
	private static Lock gamesLock = new ReentrantLock();
	//users who are currently hosting
	private static HashSet<String> hosts = new HashSet<String>();
	private static Lock hostsLock = new ReentrantLock();
	
	public static void main(String[] args) throws Exception {
		Hub hub = new Hub(Constants.HUB_KS_FILENAME, Constants.HUB_KS_PASSWORD);
		hub.openHub();
	}
	
	public Hub( String keyStoreFilename, String ksPassword ) throws Exception {
		// Load the keystore containing the Hub's private key
		this.ksPassword = ksPassword.toCharArray();
		this.keyStore = KeyStore.getInstance("JKS");
		FileInputStream ksFile = new FileInputStream(keyStoreFilename);
		try {
			keyStore.load(ksFile, this.ksPassword);
		} finally {
			ksFile.close();
		}
	}
	
	protected void openHub() throws Exception {
		SSLContext sslContext = SSLContext.getInstance("TLS");
		KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
		kmf.init(keyStore, ksPassword);
		sslContext.init(kmf.getKeyManagers(), null, null);
		SSLServerSocketFactory sf = sslContext.getServerSocketFactory();
		SSLServerSocket ss = (SSLServerSocket) sf.createServerSocket(Constants.HUB_PORT);
		
		while (true) {
			try {
				// wait for a peer to connect
				SSLSocket peer = (SSLSocket) ss.accept();
				if (peer != null) {
					HubProtocol p = selectProtocol(peer);
					if(p != null) {
						new Thread(p).start();
					}
				}
			} catch (IOException e) {
				System.out.println("Error processing client protocol");
			}
		}
	}
	
	private static HubProtocol selectProtocol(SSLSocket s) throws IOException {
		ObjectInputStream in = null;
		int id = -1;
		HubProtocol p = null;
		
		InetAddress i = s.getInetAddress();
		
		in = new ObjectInputStream(s.getInputStream());
		id = in.readInt();

		switch (id) {
		case Constants.REGISTER:
			p = new UserRegistrationProtocol(s, in);
			break;
		case Constants.LOGIN:
			p = new UserLoginProtocol(s, in);
			break;
		case Constants.GET_HOSTS:
			p = new GetHostsProtocol(s, in);
			break;
		case Constants.NEW_HOST:
			p = new NewHostProtocol(s, in);
			break;
		case Constants.JOIN_GAME:
			p = new JoinHostProtocol(s, in);
			break;
		case Constants.LOGOUT:
			p = new UserLogoutProtocol(s, in);
			break;
		case Constants.GET_LOG:
			p = new GetLogProtocol(s, in);
			break;
		default:
			System.out.println("Unrecognized protocol ID");
		}
		return p;
	}
	
	public static void loginUser(InetAddress addr, String username, int sessionKey) {
		sessionKeyLock.lock();
		addrMapLock.lock();
		try {
			sessionKeyMap.put(username, sessionKey);
			addrMap.put(username, addr);
		} finally {
			sessionKeyLock.unlock();
			addrMapLock.unlock();
		}
	}
	
	public static InetAddress getAddr(String username) {
		InetAddress output = null;
		addrMapLock.lock();
		try {
			output = addrMap.get(username);
		} finally {
			addrMapLock.unlock();
		}
		return output;
	}
	
	public static void logoutUser(String username) {
		sessionKeyLock.lock();
		addrMapLock.lock();
		hostsLock.lock();
		try {
			sessionKeyMap.remove(username);
			addrMap.remove(username);
			
		} finally {
			sessionKeyLock.unlock();
			addrMapLock.unlock();
			hostsLock.unlock();
		}
	}
	

	public static void hostNewGame(String host) {
		GameDescription gd = new GameDescription(host);
		gamesLock.lock();
		hostsLock.lock();
		try {
			games.put(host, gd);
			hosts.add(host);
		} finally {
			gamesLock.unlock();
			hostsLock.unlock();
		}
	}
	
	public static Key getGameKey(String host) {
		Key output = null;
		gamesLock.lock();
		hostsLock.lock();
		try {
			GameDescription gd = games.get(host);
			output = gd.getKey();
		} finally {
			gamesLock.unlock();
			hostsLock.unlock();
		}
		return output;
	}
	
	public static void addPlayerToGame(String host, String player) {
		gamesLock.lock();
		try {
			games.get(host).addPlayer(player);
		} finally {
			gamesLock.unlock();
		}
	}

	public static void removeGameDescription(String host) {
		gamesLock.lock();
		try {
			games.remove(host);
		} finally {
			gamesLock.unlock();
		}
	}
	
	public static final Set<String> getHosts() {
		Set<String> output = null;
		hostsLock.lock();
		try {
			output = hosts;
		} finally {
			hostsLock.unlock();
		}
		return output;
	}
	
	public static void removeHost(String host) {
		hostsLock.lock();
		try {
			hosts.remove(host);
		} finally {
			hostsLock.unlock();
		}
	}

	public static boolean verifySession(String username, int sessionKey) {
		boolean output = false;
		sessionKeyLock.lock();
		try {
			if (sessionKey == sessionKeyMap.get(username)) {
				output = true;
			}
		} finally {
			sessionKeyLock.unlock();
		}
		return output;
	}
}