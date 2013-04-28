package peer;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.KeyStore;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.SSLSocket;

import utils.Constants;
import utils.KeyStoreUtils;
import utils.NetworkUtils;
//import utils.Tuple;

public class HubGuiProtocols {	
	private static final String TS_FILE = "hub.public";
	
	public static HashMap<Integer, String> getGameList(String uname, int sessionKey) 
			throws IOException, GeneralSecurityException, ClassNotFoundException {
		KeyStore ks = KeyStoreUtils.genUserKeyStore(uname, "GetGames");
		KeyStore ts = KeyStoreUtils.genUserTrustStore(TS_FILE);

		SSLSocket s;
		ObjectOutputStream out;
		ObjectInputStream in;
		
		// Now open an SSL connection to the Hub and login as the user just registered
		s = NetworkUtils.createSecureSocket(InetAddress.getLocalHost(), Constants.HUB_PORT, ts, ks, "GetGames");

		out = new ObjectOutputStream(s.getOutputStream());
		
		out.writeObject(Constants.GET_GAMES);
		out.writeObject(uname);
		out.writeObject(sessionKey);
		
		//read the hub response to see if the credentials are valid
		in = new ObjectInputStream(s.getInputStream());
		String validResponse = (String) in.readObject();
		
		HashMap<Integer, String> games = null;
		if (validResponse.equals(Constants.VALID_SECRET)) {
			games = getGames(in);
		}
		
		return games;
	}
	
    private static HashMap<Integer, String> getGames(ObjectInputStream in) throws IOException, ClassNotFoundException {
		int numGames = (Integer) in.readObject();
		HashMap<Integer, String> games = new HashMap<Integer, String>();
    	for (int i = 0; i < numGames; i++) {
    		Integer id = (Integer) in.readObject();
    		games.put(id, (String) in.readObject());
    	}
    	return games;
    }
    
    /**
     * 
     * 
     * @return the socket connection to a peer
     * @throws ClassNotFoundException 
     * @throws GeneralSecurityException 
     */
    public static Integer hostNewGame(String gameName, int numPlayers, PublicKey signKey, String uname, int secret) throws IOException, ClassNotFoundException, GeneralSecurityException {
    	KeyStore ks = KeyStoreUtils.genUserKeyStore(uname, "GetGames");
		KeyStore ts = KeyStoreUtils.genUserTrustStore(TS_FILE);

		SSLSocket s;
		ObjectOutputStream out;
		ObjectInputStream in;
		
		// Now open an SSL connection to the Hub and login as the user just registered
		s = NetworkUtils.createSecureSocket(InetAddress.getLocalHost(), Constants.HUB_PORT, ts, ks, "GetGames");

		out = new ObjectOutputStream(s.getOutputStream());
		
		out.writeObject(Constants.HOST_GAME);
		out.writeObject(uname);
		out.writeObject(secret);
		
		//read the hub response to see if the credentials are valid
		in = new ObjectInputStream(s.getInputStream());
		String validResponse = (String) in.readObject();
    	Integer gameID = null;
		if (validResponse.equals(Constants.VALID_SECRET)) {
			out.writeObject(gameName);
			out.writeObject(numPlayers);
			out.writeObject(signKey);
			
			gameID = (Integer) in.readObject();
		}
		return gameID;
    }

	public static boolean joinGame(Integer id, PublicKey signKey, String uname, int sessionKey) throws IOException, GeneralSecurityException, ClassNotFoundException {
		KeyStore ks = KeyStoreUtils.genUserKeyStore(uname, "JOIN");
		KeyStore ts = KeyStoreUtils.genUserTrustStore(TS_FILE);

		SSLSocket s;
		ObjectOutputStream out;
		ObjectInputStream in;
		
		// Now open an SSL connection to the Hub and login as the user just registered
		s = NetworkUtils.createSecureSocket(InetAddress.getLocalHost(), Constants.HUB_PORT, ts, ks, "JOIN");

		out = new ObjectOutputStream(s.getOutputStream());
		
		out.writeObject(Constants.JOIN_GAME);
		System.out.println("Wrote JOin");

		boolean output = false;
		out.writeObject(uname);
		System.out.println("Wrote name");
		out.writeObject(sessionKey);
		System.out.println("Wrote key");
		
		in  = new ObjectInputStream(s.getInputStream());
		String verify = (String) in.readObject();
		if (verify.equals(Constants.VALID_SECRET)) {
			
			out.writeObject(id);
			out.writeObject(signKey);
			output = true;
		}
		
		return output;
	}
	
	public static int login(String uname, String password) throws IOException, ClassNotFoundException, GeneralSecurityException {
		KeyStore ks = KeyStoreUtils.genUserKeyStore(uname, new String(password));
		KeyStore ts = KeyStoreUtils.genUserTrustStore(TS_FILE);

		SSLSocket s;
		ObjectOutputStream out;
		ObjectInputStream in;
		Integer sessionSecret = -1;
		
		// Now open an SSL connection to the Hub and login as the user just registered
		s = NetworkUtils.createSecureSocket(InetAddress.getLocalHost(), Constants.HUB_PORT, ts, ks, password);

		out = new ObjectOutputStream(s.getOutputStream());
		
		out.writeObject(Constants.LOGIN);
		
		in = new ObjectInputStream(s.getInputStream());

		out.writeObject(uname);
		out.writeObject(password);

		String loginStatus = (String)in.readObject();

		if(loginStatus.equals(Constants.LOGIN_SUCCESS)) {
			sessionSecret = (Integer)in.readObject();
		}
		return sessionSecret;
	}

	public static int register(String uname, String password) throws IOException, GeneralSecurityException, ClassNotFoundException {
		KeyStore ks = KeyStoreUtils.genUserKeyStore(uname, password);
		KeyStore ts = KeyStoreUtils.genUserTrustStore(TS_FILE);

		SSLSocket s;
		ObjectOutputStream out;
		ObjectInputStream in;
		String pwRequest;
		int output;
		// Open an SSL connection to the Hub and register a new user account
		s = NetworkUtils.createSecureSocket(InetAddress.getLocalHost(), Constants.HUB_PORT, ts, ks, password);

		out = new ObjectOutputStream(s.getOutputStream());
		out.writeObject(Constants.REGISTER);

		in = new ObjectInputStream(s.getInputStream());

		out.writeObject(uname);

		pwRequest = (String)in.readObject();

		if(pwRequest.equals(Constants.REGISTRATION_PASSWORD)) {
			out.writeObject(password);

			String regStatus = (String)in.readObject();
			if (regStatus.equals(Constants.REGISTRATION_SUCCESS)) {
				output = 0;
			} else {
				output = 2;
			}
		} else if (pwRequest.equals(Constants.REGISTRATION_IN_USE)) {
			output = 1;
		} else {
			output = 2;
		}
		
		in.close();
		out.close();
		s.close();
		return output;
	}
	
	private static SSLSocket getSSLSocket(String uname, String pw) throws GeneralSecurityException, IOException {		
		KeyStore ks = KeyStoreUtils.genUserKeyStore(uname, pw);
		KeyStore ts = KeyStoreUtils.genUserTrustStore(TS_FILE);

		// Open an SSL connection to the Hub and register a new user account
		return NetworkUtils.createSecureSocket(InetAddress.getLocalHost(), Constants.HUB_PORT, ts, ks, pw);
	}
	
	public static void logout(String uname, int sessionKey) throws IOException, GeneralSecurityException{
		KeyStore ks = KeyStoreUtils.genUserKeyStore(uname, "logout");
		KeyStore ts = KeyStoreUtils.genUserTrustStore(TS_FILE);

		SSLSocket s;
		ObjectOutputStream out;
		// Open an SSL connection to the Hub and register a new user account
		s = NetworkUtils.createSecureSocket(InetAddress.getLocalHost(), Constants.HUB_PORT, ts, ks, "logout");

		out = new ObjectOutputStream(s.getOutputStream());
		
		out.writeObject(Constants.LOGOUT);
		
		out.writeObject(uname);
		out.writeObject(sessionKey);
	}

	public static List<String> getPlayerList(Integer id, String username,
			int secret) throws GeneralSecurityException, IOException, ClassNotFoundException {
		KeyStore ks = KeyStoreUtils.genUserKeyStore(username, "GetGames");
		KeyStore ts = KeyStoreUtils.genUserTrustStore(TS_FILE);

		SSLSocket s;
		ObjectOutputStream out;
		ObjectInputStream in;
		
		// Now open an SSL connection to the Hub and login as the user just registered
		s = NetworkUtils.createSecureSocket(InetAddress.getLocalHost(), Constants.HUB_PORT, ts, ks, "GetGames");

		out = new ObjectOutputStream(s.getOutputStream());
		
		out.writeObject(Constants.GET_GAME_PLAYERS);
		out.writeObject(username);
		out.writeObject(secret);
		
		//read the hub response to see if the credentials are valid
		in = new ObjectInputStream(s.getInputStream());
		String validResponse = (String) in.readObject();
		
		ArrayList<String> players = null;
		if (validResponse.equals(Constants.VALID_SECRET)) {
			out.writeObject(id);
			int numPlayers = (Integer) in.readObject();
			players = new ArrayList<String>();
			for (int i = 0; i < numPlayers; i++) {
				String pname = (String) in.readObject();
				players.add(pname);
			}
		}
		return players;
	}
	
	public static boolean leaveGame(Integer id, String username, int secret) throws GeneralSecurityException, IOException, ClassNotFoundException {
		KeyStore ks = KeyStoreUtils.genUserKeyStore(username, "GetGames");
		KeyStore ts = KeyStoreUtils.genUserTrustStore(TS_FILE);

		SSLSocket s;
		ObjectOutputStream out;
		ObjectInputStream in;
		
		// Now open an SSL connection to the Hub and login as the user just registered
		s = NetworkUtils.createSecureSocket(InetAddress.getLocalHost(), Constants.HUB_PORT, ts, ks, "GetGames");

		out = new ObjectOutputStream(s.getOutputStream());
		
		out.writeObject(Constants.LEAVE);
		out.writeObject(username);
		out.writeObject(secret);
		
		//read the hub response to see if the credentials are valid
		System.out.println("Before stream");
		in = new ObjectInputStream(s.getInputStream());
		System.out.println("Before read");
		String validResponse = (String) in.readObject();
		boolean output = false;
		
		if (validResponse.equals(Constants.VALID_SECRET)) {
			out.writeObject(id);
			output = true;
		}
		return output;
	}

	public static GameInfo ready(Integer id, String username, int secret) throws GeneralSecurityException, IOException, ClassNotFoundException {
		KeyStore ks = KeyStoreUtils.genUserKeyStore(username, "GetGames");
		KeyStore ts = KeyStoreUtils.genUserTrustStore(TS_FILE);

		SSLSocket s;
		ObjectOutputStream out;
		ObjectInputStream in;
		
		// Now open an SSL connection to the Hub and login as the user just registered
		s = NetworkUtils.createSecureSocket(InetAddress.getLocalHost(), Constants.HUB_PORT, ts, ks, "GetGames");

		out = new ObjectOutputStream(s.getOutputStream());
		
		out.writeObject(Constants.READY);
		out.writeObject(username);
		out.writeObject(secret);
		
		//read the hub response to see if the credentials are valid
		in = new ObjectInputStream(s.getInputStream());
		String validResponse = (String) in.readObject();
		
		GameInfo gi = null;
		if (validResponse.equals(Constants.VALID_SECRET)) {
			out.writeObject(id);
			
			int len = (Integer) in.readObject();
			if (len > 0) {
				Key encryptKey = (Key) in.readObject();
				List<String> players = new ArrayList<String>();
				Map<String, PublicKey> peerKeys = new HashMap<String, PublicKey>();
				Map<String, InetAddress> peerAddrs = new HashMap<String, InetAddress>(); 
				for (int i = 0; i < len; i++) {
					String pname = (String) in.readObject();
					players.add(pname);
					peerKeys.put(pname, (PublicKey) in.readObject());
					peerAddrs.put(pname, (InetAddress) in.readObject());
				}
				gi = new GameInfo(id, encryptKey, peerKeys, peerAddrs, players);
			}
		}
		return gi;
	}

}
