package peer;

import java.net.Socket;
import java.nio.ByteBuffer;
import java.security.Key;
import java.util.LinkedList;
import java.util.List;

import utils.Constants;
import utils.NetworkUtils;

public class HubGuiProtocols {
	
	public static List<String> getHostList(Key sharedKey) {
		Socket s = NetworkUtils.handleCreateSocket();
		
		NetworkUtils.sendEncryptedMessage(s, sharedKey.getEncoded(), Constants.getHubPublicKey(), Constants.PUBLIC_ENCRYPT_ALG);
		NetworkUtils.sendProtocolID(s, Constants.GET_HOSTS);
		
		List<String> hosts = getHostList(s, sharedKey);		
		return hosts;
	}
	
    private static List<String> getHostList(Socket s, Key sharedKey) {
    	int len = ByteBuffer.wrap(NetworkUtils.readEncryptedMessage(s, sharedKey, Constants.SHARED_ENCRYPT_ALG)).getInt();
    	System.out.println(len);
    	List<String> list = new LinkedList<String>();
    	for (int i = 0; i < len; i++) {
    		System.out.println(i);
    		list.add(new String(NetworkUtils.readEncryptedMessage(s, sharedKey, Constants.SHARED_ENCRYPT_ALG)));
    	}
    	return list;
    }
    
    public static void hostNewGame(Key sharedKey) {
    	Socket s = NetworkUtils.handleCreateSocket();
    	
    	NetworkUtils.sendEncryptedMessage(s, sharedKey.getEncoded(), Constants.getHubPublicKey(), Constants.PUBLIC_ENCRYPT_ALG);
    	NetworkUtils.sendProtocolID(s, Constants.NEW_HOST);
    	
    }

}
