package test;

import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import java.security.KeyStore;
import java.security.GeneralSecurityException;
import java.security.cert.Certificate;

import utils.KeyStoreUtils;
import utils.NetworkUtils;

public class JSSEServerTest {

	public static void main(String[] args) throws GeneralSecurityException, IOException, ClassNotFoundException {
		KeyStore ts = KeyStoreUtils.loadHubTrustStore("all-clients.public");
		
		ServerSocket ss = NetworkUtils.createServerSocket(4321);
		Socket client = ss.accept();
		
		ObjectOutputStream out = new ObjectOutputStream(client.getOutputStream());
		out.writeObject("Server: Certificate, please?");
		
		ObjectInputStream in = new ObjectInputStream(client.getInputStream());
		Certificate cert = (Certificate)in.readObject();
		ts.setCertificateEntry("user", cert);
		OutputStream tsOut = new FileOutputStream("all-clients.public");
		ts.store(tsOut, "public".toCharArray());
		System.out.println("Stored a certificate!");
		
		out.writeObject("Server: Thank you.");
		
		client.close();
		ss.close();
	}

}
