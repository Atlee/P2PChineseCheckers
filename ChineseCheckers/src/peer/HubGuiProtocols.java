package peer;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.security.Key;
import java.util.LinkedList;
import java.util.List;

import com.sun.corba.se.pept.transport.ContactInfo;

import utils.Constants;
import utils.NetworkUtils;

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
    public static ServerSocket hostNewGame(Key sharedKey) {
    	Socket s = NetworkUtils.handleCreateSocket();
    	
    	NetworkUtils.sendEncryptedMessage(s, sharedKey.getEncoded(), Constants.getHubPublicKey(), Constants.PUBLIC_ENCRYPT_ALG);
    	NetworkUtils.sendProtocolID(s, Constants.NEW_HOST);
    	
    	ServerSocket peer = null;
    	try {
    		peer = new ServerSocket(Constants.CLIENT_HOST_PORT);
    	} catch (IOException e) {
    		System.out.println("Could not listen on port " + Constants.CLIENT_HOST_PORT);
    		e.printStackTrace();
    		System.exit(1);
    	}
    	return peer;
    }
    
    public static void joinNewGame(String gameName, Key sharedKey) {
    	Socket s = NetworkUtils.handleCreateSocket();
    	
    	NetworkUtils.sendEncryptedMessage(s, sharedKey.getEncoded(), Constants.getHubPublicKey(), Constants.PUBLIC_ENCRYPT_ALG);
    	NetworkUtils.sendProtocolID(s, Constants.JOIN_GAME);
    }

	public static InetAddress joinGame(String hostname, Key sharedKey) throws IOException {
		Socket s = NetworkUtils.handleCreateSocket();
		
		NetworkUtils.sendEncryptedMessage(s, sharedKey.getEncoded(), Constants.getHubPublicKey(), Constants.PUBLIC_ENCRYPT_ALG);
		NetworkUtils.sendProtocolID(s, Constants.JOIN_GAME);
		NetworkUtils.sendEncryptedMessage(s, hostname.getBytes(), sharedKey, Constants.SHARED_ENCRYPT_ALG);
		byte[] hostAddrBytes = NetworkUtils.readEncryptedMessage(s, sharedKey, Constants.SHARED_ENCRYPT_ALG);
		return InetAddress.getByAddress(hostAddrBytes);
	}

}
