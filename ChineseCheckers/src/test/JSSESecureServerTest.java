package test;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import javax.net.ssl.SSLServerSocket;
import java.net.Socket;

import java.security.KeyStore;
import java.security.GeneralSecurityException;

import utils.KeyStoreUtils;
import utils.NetworkUtils;

public class JSSESecureServerTest {

	public static void main(String[] args) throws GeneralSecurityException, IOException, ClassNotFoundException {
		KeyStore ks = KeyStoreUtils.loadHubKeyStore("hub.private", "hubpassword");
		KeyStore ts = KeyStoreUtils.loadHubTrustStore("all-clients.public");
		
		SSLServerSocket ss = NetworkUtils.createSecureServerSocket(4321, ts, ks, "hubpassword");
		Socket client = ss.accept();
		
		ObjectOutputStream out = new ObjectOutputStream(client.getOutputStream());
		out.writeObject("Secure Server: Hello!");
		
		ObjectInputStream in = new ObjectInputStream(client.getInputStream());
		String reply = (String)in.readObject();
		System.out.println(reply);
		
		out.writeObject("Secure Server: Goodbye.");
		
		client.close();
		ss.close();
	}

}
