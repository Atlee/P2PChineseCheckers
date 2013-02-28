package hub;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.security.KeyStore;
import java.security.PublicKey;

import utils.MyKeyStore;
import utils.NetworkUtils;
import utils.Protocol;


public class UserRegistrationProtocol extends Protocol implements HubProtocol {
	
	private MyKeyStore ks;
	
	public UserRegistrationProtocol(MyKeyStore ks) {
		this.ks = ks;
	}
	
	public void execute(Socket s) {
		try {
			
			byte[] message;
			byte[] response;
			
			boolean usernameAvailable = false;
			String username = null;
			
			while(!usernameAvailable) {
				response = NetworkUtils.readMessage(s);
				username = new String(response);
				
				// TODO: check whether the desired username is actually available
				if (nameAvailable(username)) {
					usernameAvailable = true;
					message = ("AVAILABLE,"+username).getBytes();
				} else {
					message = ("IN_USE,"+username).getBytes();
				}
				
				NetworkUtils.sendSignedMessage(s, message, ks.getPrivateKey("hub", "password".toCharArray()));
			}
		
			PublicKey key = NetworkUtils.readPublicKey(s);
			response = NetworkUtils.readSignedMessage(s, key);
			
			if ((new String(response)).equals(username)) {
				HubCertificate cert = new HubCertificate(username, key);
				ks.addPublicKeyCertificate(username, cert);
				//sendCertificate(s, cert);
			} else {
				return;
			}
			
		} catch (IOException e) {
			System.out.println("Error executing UserRegistrationProtocol");
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	private boolean nameAvailable(String username) {
		// TODO Auto-generated method stub
		return true;
	}

}
