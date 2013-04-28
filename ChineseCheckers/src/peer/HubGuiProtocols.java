package peer;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.util.HashMap;
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
     */
    /*public Key hostNewGame() throws IOException {
    	SSLSocket s = createSecureSocket();
        ObjectOutputStream out = new ObjectOutputStream(s.getOutputStream());
        ObjectInputStream in   = new ObjectInputStream(s.getInputStream());
        
    	NetworkUtils.sendProtocolID(s, Constants.NEW_HOST);
		out.writeUTF(username);
		out.writeInt(sessionKey);
		
		String response = in.readUTF();
    	
		if (response.equals(Constants.VERIFY_SUCCESS+username)) {
			try {
				Key k = (Key) in.readObject();
				if (k != null) {
					return k;
				}
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
    	
    	return null;
    }

	public Tuple<InetAddress, Key> joinGame(String hostname) throws IOException {
		SSLSocket s = createSecureSocket();
		ObjectOutputStream out = new ObjectOutputStream(s.getOutputStream());
        ObjectInputStream in   = new ObjectInputStream(s.getInputStream());
		
		NetworkUtils.sendProtocolID(s, Constants.JOIN_GAME);
		out.writeUTF(username);
		out.writeInt(sessionKey);
		String response = in.readUTF();
		
		Tuple<InetAddress, Key> output = null;
		if (response.equals(Constants.VERIFY_SUCCESS+username)) {
			out.writeUTF(hostname);
			InetAddress addr = null;
			Key gameKey = null;
			try {
				addr = (InetAddress) in.readObject();
				gameKey = (Key) in.readObject();
				output = new Tuple<InetAddress, Key>(addr, gameKey);
			} catch (ClassNotFoundException ex) {
				System.out.println("Unknown message from Hub");
			}
		}
		return output;
	}*/
	
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

}
