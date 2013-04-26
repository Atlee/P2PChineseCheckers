package peer;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.PublicKey;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import utils.Constants;
import utils.EncryptUtils;
import utils.NetworkUtils;
import utils.Tuple;

public class HubGuiProtocols {
	
	private static void sendSharedKey(Socket s, Key sharedKey) throws IOException {
		PublicKey hubPublic = Constants.getHubPublicKey();
		
		NetworkUtils.sendEncryptedMessage(s, sharedKey.getEncoded(), hubPublic, Constants.PUBLIC_ENCRYPT_ALG);
	}
	
	public static List<String> getHostList() throws IOException {
		Socket s = NetworkUtils.handleCreateSocket();
		Key sharedKey = EncryptUtils.handleCreateSharedKey();
		
		sendSharedKey(s, sharedKey);
		NetworkUtils.sendProtocolID(s, Constants.GET_HOSTS);
		
		List<String> hosts = getHostList(s, sharedKey);
		return hosts;
	}
	
    private static List<String> getHostList(Socket s, Key sharedKey) throws IOException {
    	int len = ByteBuffer.wrap(NetworkUtils.readEncryptedMessage(s, sharedKey, Constants.SHARED_ENCRYPT_ALG)).getInt();
    	List<String> list = new LinkedList<String>();
    	for (int i = 0; i < len; i++) {
    		System.out.println(i);
    		list.add(new String(NetworkUtils.readEncryptedMessage(s, sharedKey, Constants.SHARED_ENCRYPT_ALG)));
    	}
    	return list;
    }
    
    /**
     * 
     * 
     * @return the socket connection to a peer
     */
    public static Key hostNewGame() throws IOException {
    	Socket s = NetworkUtils.handleCreateSocket();
    	Key sharedKey = EncryptUtils.handleCreateSharedKey();
    	
    	sendSharedKey(s, sharedKey);
    	NetworkUtils.sendProtocolID(s, Constants.NEW_HOST);
    	
    	try {
			Key k = EncryptUtils.getSharedKey(NetworkUtils.readEncryptedMessage(s, sharedKey, Constants.SHARED_ENCRYPT_ALG));
			if (k != null) {
				return k;
			}
		} catch (InvalidKeyException e1) {
			e1.printStackTrace();
			System.exit(1);
		}
    	
    	return null;
    }

	public static Tuple<InetAddress, Key> joinGame(String hostname) throws IOException {
		Socket s = NetworkUtils.handleCreateSocket();
		Key sharedKey = EncryptUtils.handleCreateSharedKey();
		
		sendSharedKey(s, sharedKey);
		NetworkUtils.sendProtocolID(s, Constants.JOIN_GAME);
		NetworkUtils.sendEncryptedMessage(s, hostname.getBytes(), sharedKey, Constants.SHARED_ENCRYPT_ALG);
		byte[] hostAddrBytes = NetworkUtils.readEncryptedMessage(s, sharedKey, Constants.SHARED_ENCRYPT_ALG);
		Key gameKey = null;
		try {
			gameKey = EncryptUtils.getSharedKey(NetworkUtils.readEncryptedMessage(s, sharedKey, Constants.SHARED_ENCRYPT_ALG));
		} catch (InvalidKeyException e) {
			e.printStackTrace();
			System.exit(1);
		}
		return new Tuple<InetAddress, Key>(InetAddress.getByAddress(hostAddrBytes), gameKey);
	}
	
	public static boolean login(String username, char[] password) throws IOException {
		Socket s = NetworkUtils.handleCreateSocket();
        final Key sharedKey = EncryptUtils.handleCreateSharedKey();
        
		sendSharedKey(s, sharedKey);
		NetworkUtils.sendProtocolID(s, Constants.LOGIN);		
		boolean output = false;
		
		NetworkUtils.sendEncryptedMessage(s, username.getBytes(), sharedKey, Constants.SHARED_ENCRYPT_ALG);
		NetworkUtils.sendEncryptedMessage(s, NetworkUtils.charsToBytes(password), sharedKey, Constants.SHARED_ENCRYPT_ALG);
		
		String response = new String(NetworkUtils.readEncryptedMessage(s, sharedKey, Constants.SHARED_ENCRYPT_ALG));
		if (response.equals(Constants.LOGIN_SUCCESS)) {
			output = true;
		}
		return output;
	}
	
	public static int register(String username, char[] password) throws IOException {
		Socket s = NetworkUtils.handleCreateSocket();
        final Key sharedKey = EncryptUtils.handleCreateSharedKey();
        
        sendSharedKey(s, sharedKey);
		NetworkUtils.sendProtocolID(s, Constants.REGISTER);		
		
		NetworkUtils.sendEncryptedMessage(s, username.getBytes(), sharedKey, Constants.SHARED_ENCRYPT_ALG);
		NetworkUtils.sendEncryptedMessage(s, NetworkUtils.charsToBytes(password), sharedKey, Constants.SHARED_ENCRYPT_ALG);
		//get rid of password after using
		Arrays.fill(password, ' ');
		
		String response = new String(NetworkUtils.readEncryptedMessage(s, sharedKey, Constants.SHARED_ENCRYPT_ALG));
		if (response.equals(Constants.REGISTRATION_SUCCESS + username)) {
			return 0;
		} else if (response.equals(Constants.REGISTRATION_IN_USE + username)) {
			return 1;
		} else if (response.equals(Constants.REGISTRATION_FAILURE + username)){
			return 2;
		} else {
			return 3;
		}
	}
	
	public static void logout() throws IOException{
		Socket s = NetworkUtils.handleCreateSocket();
		Key sharedKey = EncryptUtils.handleCreateSharedKey();
		
		sendSharedKey(s, sharedKey);
		NetworkUtils.sendProtocolID(s, Constants.LOGOUT);
	}

}
