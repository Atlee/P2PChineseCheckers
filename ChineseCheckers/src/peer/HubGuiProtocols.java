package peer;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.security.InvalidKeyException;
import java.security.Key;
import java.util.LinkedList;
import java.util.List;

import com.sun.corba.se.pept.transport.ContactInfo;

import utils.Constants;
import utils.EncryptUtils;
import utils.NetworkUtils;
import utils.Tuple;

public class HubGuiProtocols {
	
	public static List<String> getHostList(Key sharedKey) throws IOException {
		Socket s = NetworkUtils.handleCreateSocket();
		
		NetworkUtils.sendEncryptedMessage(s, sharedKey.getEncoded(), Constants.getHubPublicKey(), Constants.PUBLIC_ENCRYPT_ALG);
		NetworkUtils.sendProtocolID(s, Constants.GET_HOSTS);
		
		List<String> hosts = getHostList(s, sharedKey);		
		return hosts;
	}
	
    private static List<String> getHostList(Socket s, Key sharedKey) throws IOException {
    	int len = ByteBuffer.wrap(NetworkUtils.readEncryptedMessage(s, sharedKey, Constants.SHARED_ENCRYPT_ALG)).getInt();
    	System.out.println(len);
    	List<String> list = new LinkedList<String>();
    	for (int i = 0; i < len; i++) {
    		System.out.println(i);
    		list.add(new String(NetworkUtils.readEncryptedMessage(s, sharedKey, Constants.SHARED_ENCRYPT_ALG)));
    	}
    	return list;
    }
    
    /**
     * 
     * @param sharedKey
     * @return the socket connection to a peer
     */
    public static Key hostNewGame(Key sharedKey) {
    	Socket s = NetworkUtils.handleCreateSocket();
    	
    	try {
    		NetworkUtils.sendEncryptedMessage(s, sharedKey.getEncoded(), Constants.getHubPublicKey(), Constants.PUBLIC_ENCRYPT_ALG);
    	} catch (IOException e) {
    		e.printStackTrace();
    		System.exit(1);
    	}
    	NetworkUtils.sendProtocolID(s, Constants.NEW_HOST);
    	
    	try {
			Key k = EncryptUtils.getSharedKey(NetworkUtils.readEncryptedMessage(s, sharedKey, Constants.SHARED_ENCRYPT_ALG));
			if (k != null) {
				return k;
			}
		} catch (IOException | InvalidKeyException e1) {
			e1.printStackTrace();
			System.exit(1);
		}
    	
    	return null;
    }

	public static Tuple<InetAddress, Key> joinGame(String hostname, Key sharedKey) throws IOException {
		Socket s = NetworkUtils.handleCreateSocket();
		
		NetworkUtils.sendEncryptedMessage(s, sharedKey.getEncoded(), Constants.getHubPublicKey(), Constants.PUBLIC_ENCRYPT_ALG);
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
	
	public static void logout(Key sharedKey) {
		Socket s = NetworkUtils.handleCreateSocket();
		
		try {
			NetworkUtils.sendEncryptedMessage(s, sharedKey.getEncoded(), Constants.getHubPublicKey(), Constants.PUBLIC_ENCRYPT_ALG);
			NetworkUtils.sendProtocolID(s, Constants.LOGOUT);
			
		} catch (IOException e) {
			System.out.println("User not logged out");
			e.printStackTrace();
			System.exit(1);
		}
	}

}
