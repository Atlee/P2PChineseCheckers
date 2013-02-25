package hub;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.security.KeyStore;
import java.security.PublicKey;

import utils.KeyStoreUtils;


public class UserRegistrationProtocol extends HubProtocol {
	
	private String username = null;
	
	public void execute(Socket s, KeyStore ks) {
		try {
			
			byte[] message;
			byte[] response;
			
			boolean usernameAvailable = false;
			while(!usernameAvailable) {
				response = readMessage(s);
				username = new String(response);
				
				// TODO: check whether the desired username is actually available
				
				usernameAvailable = true;
				message = ("AVAILABLE,"+username).getBytes();
				sendSignedMessage(s, message, KeyStoreUtils.getHubPrivateKey());
			}
		
			PublicKey key = readPublicKey(s);
			response = readSignedMessage(s, key);
			
			// TODO: make sure that the key hash in response actually equals key.hashCode()
			
			HubCertificate cert = new HubCertificate(username, key);
			KeyStoreUtils.addPublicKeyCertificate(ks, cert, username);
			
			message = ByteBuffer.allocate(4).putInt(cert.hashCode()).array();
			sendSignedMessage(s, message, KeyStoreUtils.getHubPrivateKey());
			sendCertificate(s, cert);
			
		} catch (IOException e) {
			System.out.println("Error executing UserRegistrationProtocol\n");
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	private PublicKey readPublicKey(Socket s) throws IOException {
		ObjectInputStream in = new ObjectInputStream(s.getInputStream());
		PublicKey key = null;
		try {
			key = (PublicKey) in.readObject();
		} catch (ClassNotFoundException e) {
			System.out.println("Error reading public key received from peer\n");
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
