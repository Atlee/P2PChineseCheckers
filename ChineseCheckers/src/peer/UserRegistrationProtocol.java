package peer;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.PublicKey;
import java.security.cert.Certificate;

import utils.Constants;
import utils.KeyStoreUtils;
import utils.SignUtils;


public class UserRegistrationProtocol extends PeerProtocol {
	
	private String username;
	private String password;
	
	public void execute(Socket s, KeyStore ks) {
		try {
			sendProtocolID(s);
			
			byte[] message;
			byte[] response;
			
			boolean usernameAvailable = false;
			
			while(!usernameAvailable) {
				getDesiredCredentials();
				message = (username).getBytes();
				sendMessage(s, message);
				response = readSignedMessage(s, KeyStoreUtils.getHubPublicKey());
				String responseStr = new String(response);
				if(responseStr.equals("AVAILABLE,"+username)){
					usernameAvailable = true;
				} else if(responseStr.equals("IN USE,"+username)) {
					System.out.println("The username you have selected is already in use. Please try again.\n");
				} else {
					System.out.println("lolwat\n");
					System.exit(1);
				}
			}
			
			KeyPair keys = SignUtils.newSignKeyPair();
			
			byte[] pubKeyHash = ByteBuffer.allocate(4).putInt(keys.getPublic().hashCode()).array();
			
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			outputStream.write((username+",").getBytes());
			outputStream.write(pubKeyHash);
			message = outputStream.toByteArray();
			
			sendKey(s, keys.getPublic());
			sendSignedMessage(s, message, keys.getPrivate());
			
			response = readSignedMessage(s, KeyStoreUtils.getHubPublicKey());
			Certificate cert = readCertificate(s);
			
			if(cert.hashCode() == ByteBuffer.wrap(response).getInt()){
				KeyStoreUtils.addPrivateKey(ks, keys.getPrivate(), cert, username, password);
				System.out.println("Registration successful! Welcome, "+username+".\n");
			} else {
				System.out.println("Registration failed. Please try again.\n");
			}
			
		} catch (IOException e) {
			System.out.println("Error executing UserRegistrationProtocol\n");
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	private void sendProtocolID(Socket s) throws IOException {
		DataOutputStream out = new DataOutputStream(s.getOutputStream());
		out.writeInt(Constants.REGISTER);
	}
	
	private void sendKey(Socket s, PublicKey key) throws IOException {
		ObjectOutputStream out = new ObjectOutputStream(s.getOutputStream());
		out.writeObject(key);
	}
	
	private Certificate readCertificate(Socket s) throws IOException {
		ObjectInputStream in = new ObjectInputStream(s.getInputStream());
		Certificate cert = null;
		try {
			cert = (Certificate) in.readObject();
		} catch (ClassNotFoundException e) {
			System.out.println("Error reading certificate received from Hub\n");
			e.printStackTrace();
			System.exit(1);
		}
		return cert;
	}
	
 	private void getDesiredCredentials() {
		BufferedReader stdin = new BufferedReader(
				new InputStreamReader(System.in));
		try {
			System.out.println("Desired username...\n");
			username = stdin.readLine();
			System.out.println("Password...\n");
			password = stdin.readLine();
		} catch (IOException e) {
			System.out.println("Error reading username and password\n");
			e.printStackTrace();
			System.exit(1);
		}
	}
 	
 	
}
