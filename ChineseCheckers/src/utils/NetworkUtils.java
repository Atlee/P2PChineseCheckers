package utils;

import hub.HubCertificate;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.UUID;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.SecretKeySpec;

import com.sun.crypto.provider.DESKeyFactory;

public class NetworkUtils {

	public static void sendProtocolID(Socket s, int protocolID) {
		try {
			DataOutputStream out = new DataOutputStream(s.getOutputStream());
			out.writeInt(protocolID);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	public static void sendKey(Socket s, PublicKey key) {
		try {
			ObjectOutputStream out = new ObjectOutputStream(s.getOutputStream());
			out.writeObject(key);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	public static HubCertificate readCertificate(Socket s) {
		HubCertificate cert = null;
		try {
			ObjectInputStream in = new ObjectInputStream(s.getInputStream());
			cert = (HubCertificate) in.readObject();
		} catch (ClassNotFoundException | IOException e) {
			System.out.println("Error reading certificate received from Hub");
			e.printStackTrace();
			System.exit(1);
		}
		return cert;
	}
 	
	public static Socket handleCreateSocket() {
		Socket s = null;		
		try {
			//TODO: change to the hub's InetAddress
			InetAddress host = InetAddress.getLocalHost();
			s = new Socket(host, Constants.HUB_PORT);
		} catch (IOException e) {
			System.out.println("Error creating socket");
			e.printStackTrace();
			System.exit(1);
		}
		return s;
	}
	
	public static void sendSignedMessage(Socket s, byte[] message, PrivateKey key) {
		byte[] signature = SignUtils.signData(key, message);
		sendMessage(s, signature);
		sendMessage(s, message);
	}
	
	public static void sendEncryptedMessage(Socket s, byte[] message, Key key, String encryptAlg) {
		byte[] cipherText = EncryptUtils.encryptData(message, key, encryptAlg);
		sendMessage(s, cipherText);
	}
	
	public static void sendMessage(Socket s, byte[] message) {
		try {
			DataOutputStream out = new DataOutputStream(s.getOutputStream());
			out.writeInt(message.length);
			out.write(message);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	public static byte[] readSignedMessage(Socket s, PublicKey key) {
		byte[] signature = null;
		byte[] message = null;
		try {
			DataInputStream in = new DataInputStream(s.getInputStream());
			
			int sigLen = in.readInt();
			signature = new byte[sigLen];
			in.read(signature, 0, sigLen);
			
			int messageLen = in.readInt();
			message = new byte[messageLen];
			in.read(message, 0, messageLen);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		boolean success = SignUtils.verifyData(key, signature, message);
		if (success) {
			return message;
		} else {
			return null;
		}
	}
	
	public static byte[] readEncryptedMessage(Socket s, Key key, String encryptAlg) {
		byte[] cipherText = readMessage(s);
		byte[] message = EncryptUtils.decryptData(cipherText, key, encryptAlg);
		return message;
	}
	
	public static byte[] readMessage(Socket s) {
		byte[] output = null;
		try {
			DataInputStream in = new DataInputStream(s.getInputStream());
			int len = in.readInt();
			output = new byte[len];
			in.read(output, 0, len);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		return output;
	}
	
	public static PublicKey readPublicKey(Socket s) throws IOException {
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
	
	public static void sendCertificate(Socket s, HubCertificate cert) throws IOException {
		ObjectOutputStream out = new ObjectOutputStream(s.getOutputStream());
		out.writeObject(cert);
	}
	
	public static void sendUUID(Socket s, UUID id) {
		try {
			ObjectOutputStream out = new ObjectOutputStream(s.getOutputStream());
			out.writeObject(id);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	//move to general utils file?
	public static byte[] charsToBytes(char[] c) {
		byte[] output = new byte[c.length];
		for (int i = 0; i < c.length; i++) {
			output[i] = (byte) c[i];
		}
		return output;
	}
	
	public static char[] bytesToChars(byte[] b) {
		char[] output = new char[b.length];
		for (int i = 0; i < b.length; i++) {
			output[i] = (char) b[i];
		}
		return output;
	}
}
