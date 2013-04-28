package peer;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import javax.net.ssl.SSLSocket;

import utils.Constants;
import utils.KeyStoreUtils;
import utils.NetworkUtils;
//import utils.Tuple;

public class HubGuiProtocols {	
	private static final String TS_FILE = "hub.public";
	
	public static HashMap<UUID, String> getGameList(String uname, int sessionKey) 
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
		
		HashMap<UUID, String> games = null;
		if (validResponse.equals(Constants.VALID_SECRET)) {
			games = getGames(in);
		}
		
		return games;
	}
	
    private static HashMap<UUID, String> getGames(ObjectInputStream in) throws IOException, ClassNotFoundException {
		int numGames = (Integer) in.readObject();
		HashMap<UUID, String> games = new HashMap<UUID, String>();
    	for (int i = 0; i < numGames; i++) {
    		UUID id = (UUID) in.readObject();
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
    public static UUID hostNewGame(String gameName, int numPlayers, PublicKey signKey, String uname, int secret) throws IOException, ClassNotFoundException, GeneralSecurityException {
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
		out.writeObject(secret);
		
		//read the hub response to see if the credentials are valid
		in = new ObjectInputStream(s.getInputStream());
		String validResponse = (String) in.readObject();
    	UUID gameID = null;
		if (validResponse.equals(Constants.VALID_SECRET)) {
			out.writeObject(gameName);
			out.writeObject(numPlayers);
			out.writeObject(signKey);
			
			gameID = (UUID) in.readObject();
		}
		return gameID;
    }

	public static void joinGame(UUID id, PublicKey signKey, String uname, int sessionKey) throws IOException, GeneralSecurityException {
		KeyStore ks = KeyStoreUtils.genUserKeyStore(uname, "JOIN");
		KeyStore ts = KeyStoreUtils.genUserTrustStore(TS_FILE);

		SSLSocket s;
		ObjectOutputStream out;
		
		// Now open an SSL connection to the Hub and login as the user just registered
		s = NetworkUtils.createSecureSocket(InetAddress.getLocalHost(), Constants.HUB_PORT, ts, ks, "JOIN");

		out = new ObjectOutputStream(s.getOutputStream());
		
		out.writeObject(Constants.JOIN_GAME);

		out.writeObject(uname);
		out.writeObject(sessionKey);
		
		out.writeObject(id);
		out.writeObject(signKey);
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

	public static List<String> getPlayerList(UUID id, String username,
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
			int numPlayers = (Integer) in.readObject();
			players = new ArrayList<String>();
			for (int i = 0; i < numPlayers; i++) {
				players.add((String) in.readObject());
			}
		}
		return players;
	}

	public static void ready(UUID id, String username, int secret) {
		
	}

}
