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
	private final KeyStore ks;
	private final KeyStore ts;
	private int sessionKey;
	private final String username;
	private final char[] password;
	
	public HubGuiProtocols(String username, char[] password) throws GeneralSecurityException, IOException {
		ks = KeyStoreUtils.genUserKeyStore(username, new String(password));
		ts = KeyStoreUtils.genUserTrustStore(TS_FILE);
		
		this.username = username;
		this.password = password;
	}
	
	public List<String> getHostList() throws IOException {
		SSLSocket s = createSecureSocket();
		
		NetworkUtils.sendProtocolID(s, Constants.GET_HOSTS);
		
		List<String> hosts = getHostList(s);
		return hosts;
	}
	
    private List<String> getHostList(SSLSocket s) throws IOException {
    	ObjectInputStream in = new ObjectInputStream(s.getInputStream());
    	int len = in.readInt();
    	List<String> list = new LinkedList<String>();
    	for (int i = 0; i < len; i++) {
    		System.out.println(i);
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
	
	public boolean login(String username, char[] password) throws IOException {
		SSLSocket s = createSecureSocket();
        ObjectOutputStream out = new ObjectOutputStream(s.getOutputStream());
        ObjectInputStream in   = new ObjectInputStream(s.getInputStream());
		
		NetworkUtils.sendProtocolID(s, Constants.LOGIN);		
		boolean output = false;
		
		out.writeUTF(username);
		out.writeUTF(new String(password));
		
		String response = in.readUTF();
		if (response.equals(Constants.LOGIN_SUCCESS)) {
			sessionKey = in.readInt();
			output = true;
		}
		return output;
	}
	
	private SSLSocket createSecureSocket() throws IOException {
		return NetworkUtils.createSecureSocket(ts, ks, password);
	}

	public int register(String username, char[] password) throws IOException {		
		SSLSocket s = createSecureSocket();
		
        ObjectOutputStream out = new ObjectOutputStream(s.getOutputStream());
        ObjectInputStream in   = new ObjectInputStream(s.getInputStream());
        
        NetworkUtils.sendProtocolID(s, Constants.REGISTER);		
		
		out.writeUTF(username);
		out.writeUTF(new String(password));
		//get rid of password after using
		Arrays.fill(password, ' ');
		
		String response = in.readUTF();
		
		int output = 3;
		if (response.equals(Constants.REGISTRATION_SUCCESS + username)) {
			output = 0;
		} else if (response.equals(Constants.REGISTRATION_IN_USE + username)) {
			output = 1;
		} else if (response.equals(Constants.REGISTRATION_FAILURE + username)){
			output = 2;
		} else {
			output = 3;
		}
		try {
			out.close();
			in.close();
			s.close();
		} catch (IOException ex) {
			//if closing doesn't work they were closed on the hub side
			;
		}
		return output;
	}
	
	public void logout() throws IOException{
		SSLSocket s = createSecureSocket();
        ObjectOutputStream out = new ObjectOutputStream(s.getOutputStream());
        
        NetworkUtils.sendProtocolID(s, Constants.REGISTER);		
		
		out.writeUTF(username);
		out.writeInt(sessionKey);
	}

}
