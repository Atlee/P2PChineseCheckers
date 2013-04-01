package test;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.cert.Certificate;

import utils.Constants;
import utils.KeyStoreUtils;
import utils.NetworkUtils;


public class JSSEPeerTest {

	public static void main(String[] args) throws GeneralSecurityException, IOException, ClassNotFoundException {
		String uname = args[0];
		String password = args[1];
		
		KeyStore ks = KeyStoreUtils.genUserKeyStore(uname, password);
		KeyStore ts = KeyStoreUtils.genUserTrustStore("hub.public");
		
		Socket s;
		ObjectOutputStream out;
		ObjectInputStream in;
		String unameRequest;
		String pwRequest;
		String certRequest;

		// Open an SSL connection to the login server and register a new user account
		s = NetworkUtils.createSecureSocket(InetAddress.getLocalHost(), Constants.LOGIN_SERVER_PORT, ts, ks, password);
		
		System.out.println("(Registering a new user...)");
		
		out = new ObjectOutputStream(s.getOutputStream());
		out.writeObject("REGISTER");
		
		in = new ObjectInputStream(s.getInputStream());
		unameRequest = (String)in.readObject();
		System.out.println(unameRequest);
		
		System.out.println("(Sending username...)");
		out.writeObject(uname);
		
		pwRequest = (String)in.readObject();
		System.out.println(pwRequest);

		if(pwRequest.equals("Login Server: Password, please?")) {
			System.out.println("(Sending password...)");
			out.writeObject(password);
			
			certRequest = (String)in.readObject();
			System.out.println(certRequest);
			
			System.out.println("(Sending certificate...)");
			Certificate cert = ks.getCertificate(uname);
			out.writeObject(cert);

			String regStatus = (String)in.readObject();
			System.out.println(regStatus + "\n");
		}
		
		in.close();
		out.close();
		s.close();
		
		// Now open an SSL connection to the login server and login as the user just registered
		s = NetworkUtils.createSecureSocket(InetAddress.getLocalHost(), Constants.LOGIN_SERVER_PORT, ts, ks, password);
		
		System.out.println("(Attempting to login...)");
		
		out = new ObjectOutputStream(s.getOutputStream());
		out.writeObject("LOGIN");
		
		in = new ObjectInputStream(s.getInputStream());
		unameRequest = (String)in.readObject();
		System.out.println(unameRequest);
		
		System.out.println("(Sending username...)");
		out.writeObject(uname);
		
		pwRequest = (String)in.readObject();
		System.out.println(pwRequest);
		
		System.out.println("(Sending password...)");
		out.writeObject(password);
		
		String loginStatus = (String)in.readObject();
		System.out.println(loginStatus + "\n");
		
		// Now make sure the logged in user can access hub services
		s = NetworkUtils.createSecureSocket(InetAddress.getLocalHost(), Constants.HUB_PORT, ts, ks, password);
		
		System.out.println("(Accessing hub services...)");
		
		in = new ObjectInputStream(s.getInputStream());
		String greeting = (String)in.readObject();
		System.out.println(greeting);
		
		out = new ObjectOutputStream(s.getOutputStream());
		out.writeObject(uname + ": Hi!");
		
		String farewell = (String)in.readObject();
		System.out.println(farewell);
		
		in.close();
	    out.close();
	    s.close();
	    
	}

}
