package utils;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.Key;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.util.UUID;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

public class NetworkUtils {

	public static void sendProtocolID(SSLSocket s, int protocolID) throws IOException {
		ObjectOutputStream out = new ObjectOutputStream(s.getOutputStream());
		out.writeInt(protocolID);
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
	
	public static Socket createSocket( InetAddress host, int port ) {
		Socket s = null;
		try {
			s = new Socket(host, port);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return s;
	} 
	
	public static ServerSocket createServerSocket( int port ) {
		ServerSocket ss = null;
		try {
			ss = new ServerSocket(port);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return ss;
	}
	
	public static SSLSocket createSecureSocket(KeyStore truststore, KeyStore keystore, char[] passphrase) throws IOException {
		return createSecureSocket(InetAddress.getLocalHost(), Constants.HUB_PORT, truststore, keystore, new String(passphrase));
	}
	
	public static SSLSocket createSecureSocket( InetAddress host, int port , KeyStore truststore, KeyStore keystore, String passphrase ) {
		SecureRandom sRand = new SecureRandom();
	    sRand.nextInt();
	    
	    TrustManagerFactory tmf = null;
		try {
			tmf = TrustManagerFactory.getInstance( "SunX509" );
		    tmf.init(truststore);
		} catch (NoSuchAlgorithmException | KeyStoreException e) {
			e.printStackTrace();
		}
	    
	    KeyManagerFactory kmf = null;
	    try {
		    kmf = KeyManagerFactory.getInstance( "SunX509" );
			kmf.init(keystore, passphrase.toCharArray());
		} catch (UnrecoverableKeyException | KeyStoreException | NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	    
	    SSLContext sslContext = null;
		try {
			sslContext = SSLContext.getInstance( "TLS" );
		    sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), sRand);
		} catch (NoSuchAlgorithmException | KeyManagementException e) {
			e.printStackTrace();
		}
		
        SSLSocketFactory sf = sslContext.getSocketFactory();
        
        SSLSocket socket = null;
		try {
			socket = (SSLSocket)sf.createSocket(host, port);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return socket;
	}
	
	public static SSLServerSocket createSecureServerSocket( int port , KeyStore truststore, KeyStore keystore, String passphrase ) {
		SecureRandom sRand = new SecureRandom();
	    sRand.nextInt();
	    
	    TrustManagerFactory tmf = null;
		try {
			tmf = TrustManagerFactory.getInstance( "SunX509" );
		    tmf.init(truststore);
		} catch (NoSuchAlgorithmException | KeyStoreException e) {
			e.printStackTrace();
		}
	    
	    KeyManagerFactory kmf = null;
	    try {
		    kmf = KeyManagerFactory.getInstance( "SunX509" );
			kmf.init(keystore, passphrase.toCharArray());
		} catch (UnrecoverableKeyException | KeyStoreException | NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	    
	    SSLContext sslContext = null;
		try {
			sslContext = SSLContext.getInstance( "TLS" );
		    sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), sRand);
		} catch (NoSuchAlgorithmException | KeyManagementException e) {
			e.printStackTrace();
		}
		
        SSLServerSocketFactory sf = sslContext.getServerSocketFactory();
        
        SSLServerSocket ssocket = null;
		try {
			ssocket = (SSLServerSocket)sf.createServerSocket(port);
	        ssocket.setNeedClientAuth(true);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return ssocket;
	}

	public static void sendSignedMessage(Socket s, byte[] message, PrivateKey key) throws IOException {
		byte[] signature = SignUtils.signData(key, message);
		sendMessage(s, signature);
		sendMessage(s, message);
	}
	
	public static void sendEncryptedMessage(Socket s, byte[] message, Key key, String encryptAlg) throws IOException {
		byte[] cipherText = EncryptUtils.encryptData(message, key, encryptAlg);
		sendMessage(s, cipherText);
	}
	
	public static void sendMessage(Socket s, byte[] message) throws IOException {
		DataOutputStream out = new DataOutputStream(s.getOutputStream());
		out.writeInt(message.length);
		out.write(message);
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
	
	public static byte[] readEncryptedMessage(Socket s, Key key, String encryptAlg) throws IOException {
		byte[] cipherText = readMessage(s);
		byte[] message = EncryptUtils.decryptData(cipherText, key, encryptAlg);
		return message;
	}
	
	public static byte[] readMessage(Socket s) throws IOException {
		byte[] output = null;

		DataInputStream in = new DataInputStream(s.getInputStream());
		int len = in.readInt();
		output = new byte[len];
		in.read(output, 0, len);

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
