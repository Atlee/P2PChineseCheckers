package test;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

import java.security.KeyStore;
import java.security.GeneralSecurityException;
import java.security.cert.Certificate;

import utils.KeyStoreUtils;
import utils.NetworkUtils;

public class JSSEClientTest {

	public static void main(String[] args) throws GeneralSecurityException, IOException, ClassNotFoundException {
		KeyStore ks = KeyStoreUtils.genUserKeyStore("user", "password");
		Socket s = NetworkUtils.createSocket(InetAddress.getLocalHost(), 4321);
		
		ObjectInputStream in = new ObjectInputStream(s.getInputStream());
		String certRequest = (String)in.readObject();
		System.out.println(certRequest);
		
		System.out.println("Sending certificate...");
		ObjectOutputStream out = new ObjectOutputStream(s.getOutputStream());
		Certificate cert = ks.getCertificate("user");
		out.writeObject(cert);
		
		String certAck = (String)in.readObject();
		System.out.println(certAck);
	}

}
