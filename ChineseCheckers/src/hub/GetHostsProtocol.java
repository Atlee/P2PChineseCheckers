package hub;

import java.net.Socket;
import java.nio.ByteBuffer;
import java.security.Key;
import java.util.List;

import utils.Constants;
import utils.NetworkUtils;

public class GetHostsProtocol implements HubProtocol {

	@Override
	public void execute(Socket s, Key sharedKey) {
		// TODO Auto-generated method stub
		System.out.println("in get hosts");
		List<User> hosts = Hub.getUserLogin();
		byte[] listLenBytes = ByteBuffer.allocate(4).putInt(hosts.size()).array();
		NetworkUtils.sendEncryptedMessage(s, listLenBytes, sharedKey, Constants.SHARED_ENCRYPT_ALG);
		
		for (int i = 0; i < hosts.size(); i++) {
			NetworkUtils.sendEncryptedMessage(s, hosts.get(i).getUsername().getBytes(), sharedKey, Constants.SHARED_ENCRYPT_ALG);
		}
	}
}
