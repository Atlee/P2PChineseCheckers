package test;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.cert.Certificate;

import javax.net.ssl.SSLSocket;

import utils.Constants;
import utils.KeyStoreUtils;
import utils.NetworkUtils;

public class JSSEPeerTest {

	public static void main(String[] args) throws GeneralSecurityException, IOException, ClassNotFoundException {
		String uname = args[0];
		String password = args[1];
		
		KeyStore ks = KeyStoreUtils.genUserKeyStore(uname, password);
		KeyStore ts = KeyStoreUtils.genUserTrustStore("hub.public");
		
		// Open a non-SSL connection to the new user registration server
		Socket s = NetworkUtils.createSocket(InetAddress.getLocalHost(), Constants.NEW_USER_REG_PORT);
		
		ObjectInputStream in = new ObjectInputStream(s.getInputStream());
		String unameRequest = (String)in.readObject();
		System.out.println(unameRequest);
		
		System.out.println("(Sending username...)");
		ObjectOutputStream out = new ObjectOutputStream(s.getOutputStream());
		out.writeObject(uname);
		
		String certRequest = (String)in.readObject();
		System.out.println(certRequest);
		
		System.out.println("(Sending certificate...)");
		Certificate cert = ks.getCertificate(uname);
		out.writeObject(cert);
		
		String certAck = (String)in.readObject();
		System.out.println(certAck);
		
		in.close();
		out.close();
		s.close();
		
		// Now we can open an SSL connection to the hub
		s = NetworkUtils.createSecureSocket(InetAddress.getLocalHost(), Constants.HUB_SSL_PORT, ts, ks, password);
		
		in = new ObjectInputStream(s.getInputStream());
		String greeting = (String)in.readObject();
		System.out.println(greeting);
		
		out = new ObjectOutputStream(s.getOutputStream());
		out.writeObject("SSL Client: Hi!");
		
		String farewell = (String)in.readObject();
		System.out.println(farewell);
		
		in.close();
	    out.close();
	    s.close();
	}

}
