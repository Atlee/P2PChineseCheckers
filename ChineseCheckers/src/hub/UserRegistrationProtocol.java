package hub;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.security.KeyStore;
import java.security.PublicKey;

import utils.KeyStoreUtils;
import utils.Protocol;


public class UserRegistrationProtocol extends Protocol {
	
	public void execute(Socket s, KeyStore ks) {
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
				
				sendSignedMessage(s, message, KeyStoreUtils.getPrivateKey(ks, "hub", "password"));
			}
		
			PublicKey key = readPublicKey(s);
			response = readSignedMessage(s, key);
			
			if ((new String(response)).equals(username)) {
				HubCertificate cert = new HubCertificate(username, key);
				KeyStoreUtils.addPublicKeyCertificate(ks, username, cert);
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
