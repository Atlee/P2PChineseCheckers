package hub;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.security.KeyStore;
import java.security.PublicKey;

import utils.MyKeyStore;
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
				response = readMessage(s);
				username = new String(response);
				
				// TODO: check whether the desired username is actually available
				if (nameAvailable(username)) {
					usernameAvailable = true;
					message = ("AVAILABLE,"+username).getBytes();
				} else {
					message = ("IN_USE,"+username).getBytes();
				}
				
				sendSignedMessage(s, message, ks.getPrivateKey("hub", "password".toCharArray()));
			}
		
			PublicKey key = readPublicKey(s);
			response = readSignedMessage(s, key);
			
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

	private PublicKey readPublicKey(Socket s) throws IOException {
		ObjectInputStream in = new ObjectInputStream(s.getInputStream());
		PublicKey key = null;
		try {
			key = (PublicKey) in.readObject();
		} catch (ClassNotFoundException e) {
			System.out.println("Error reading public key received from peer");
			e.printStackTrace();
			System.exit(1);
		}
		return key;
	}
	
	private void sendCertificate(Socket s, HubCertificate cert) throws IOException {
		ObjectOutputStream out = new ObjectOutputStream(s.getOutputStream());
		out.writeObject(cert);
	}

}
