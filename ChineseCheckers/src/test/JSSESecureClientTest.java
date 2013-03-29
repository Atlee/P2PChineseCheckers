package test;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import javax.net.ssl.SSLSocket;

import java.security.KeyStore;
import java.security.GeneralSecurityException;

import utils.KeyStoreUtils;
import utils.NetworkUtils;

public class JSSESecureClientTest {

	public static void main(String[] args) throws GeneralSecurityException, IOException, ClassNotFoundException {
		KeyStore ks = KeyStoreUtils.genUserKeyStore("user", "password");
		KeyStore ts = KeyStoreUtils.genUserTrustStore("hub.public");
		SSLSocket s = NetworkUtils.createSecureSocket(InetAddress.getLocalHost(), 4321, ts, ks, "password");
		
		ObjectInputStream in = new ObjectInputStream(s.getInputStream());
		String greeting = (String)in.readObject();
		System.out.println(greeting);
		
		ObjectOutputStream out = new ObjectOutputStream(s.getOutputStream());
		out.writeObject("Secure Client: Hi!");
		
		String farewell = (String)in.readObject();
		System.out.println(farewell);
	}

}
