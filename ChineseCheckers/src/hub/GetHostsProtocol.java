package hub;

import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.security.Key;
import java.util.HashMap;

import utils.Constants;
import utils.NetworkUtils;

public class GetHostsProtocol implements HubProtocol {

	@Override
	public void execute(Socket s, Key sharedKey) {
		HashMap<String, User> hosts = Hub.getUserHost();
		byte[] listLenBytes = ByteBuffer.allocate(4).putInt(hosts.size()).array();
		try {
			NetworkUtils.sendEncryptedMessage(s, listLenBytes, sharedKey, Constants.SHARED_ENCRYPT_ALG);
			
			for (String hostname : hosts.keySet()) {
				NetworkUtils.sendEncryptedMessage(s, hostname.getBytes(), sharedKey, Constants.SHARED_ENCRYPT_ALG);
			}
		} catch (IOException e) {
			System.out.println("Error GetHostProtocol");
			e.printStackTrace();
		}
	}
}
