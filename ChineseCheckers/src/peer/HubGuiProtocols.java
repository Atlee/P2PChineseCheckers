package peer;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyStore;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import javax.net.ssl.SSLSocket;

import utils.Constants;
import utils.EncryptUtils;
import utils.KeyStoreUtils;
import utils.NetworkUtils;
import utils.Tuple;

public class HubGuiProtocols {	
	private static final String TS_FILE = "hub.public";
	private int sessionKey;
	
	public static List<String> getHostList() throws IOException {
		SSLSocket s = getSSLSocket("getHost", "getHost");
		
		NetworkUtils.sendProtocolID(s, Constants.GET_HOSTS);
		
		List<String> hosts = getHostList(s);
		return hosts;
	}
	
    private static List<String> getHostList(SSLSocket s) throws IOException {
    	ObjectInputStream in = new ObjectInputStream(s.getInputStream());
    	int len = in.readInt();
    	List<String> list = new LinkedList<String>();
    	for (int i = 0; i < len; i++) {
    		list.add(in.readUTF());
    	}
    	return list;
    }
    
    /**
     * 
     * 
     * @return the socket connection to a peer
     */
    public Key hostNewGame() throws IOException {
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
	}
	
	public int login(String uname, String password) throws IOException, ClassNotFoundException, GeneralSecurityException {
		KeyStore ks = KeyStoreUtils.genUserKeyStore(uname, new String(password));
		KeyStore ts = KeyStoreUtils.genUserTrustStore(TS_FILE);

		SSLSocket s;
		ObjectOutputStream out;
		ObjectInputStream in;
		Integer sessionSecret = -1;
		
		// Now open an SSL connection to the Hub and login as the user just registered
		s = NetworkUtils.createSecureSocket(InetAddress.getLocalHost(), Constants.HUB_PORT, ts, ks, password);

		System.out.println("(Attempting to login...)");

		out = new ObjectOutputStream(s.getOutputStream());
		out.writeObject(Constants.LOGIN);

		in = new ObjectInputStream(s.getInputStream());

		System.out.println("(Sending username...)");
		out.writeObject(uname);

		System.out.println("(Sending password...)");
		out.writeObject(password);

		String loginStatus = (String)in.readObject();
		System.out.println(loginStatus + "\n");

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
		String unameRequest;
		String pwRequest;
		int output;
		// Open an SSL connection to the Hub and register a new user account
		s = NetworkUtils.createSecureSocket(InetAddress.getLocalHost(), Constants.HUB_PORT, ts, ks, password);

		System.out.println("(Registering a new user...)");

		out = new ObjectOutputStream(s.getOutputStream());
		out.writeObject(Constants.REGISTER);

		in = new ObjectInputStream(s.getInputStream());
		unameRequest = (String)in.readObject();
		System.out.println(unameRequest);

		System.out.println("(Sending username...)");
		out.writeObject(uname);

		pwRequest = (String)in.readObject();
		System.out.println(pwRequest);

		if(pwRequest.equals(Constants.REGISTRATION_PASSWORD)) {
			System.out.println("(Sending password...)");
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
		SSLSocket s = getSSLSocket("logout", "logout");

		System.out.println("(Registering a new user...)");

		ObjectOutputStream out = new ObjectOutputStream(s.getOutputStream());
		out.writeObject(Constants.LOGOUT);

		
		out.writeObject(uname);
		out.writeInt(sessionKey);
	}

}
