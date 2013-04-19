package test;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.security.GeneralSecurityException;
import java.security.KeyStore;

import javax.net.ssl.SSLSocket;

import utils.Constants;
import utils.KeyStoreUtils;
import utils.NetworkUtils;


public class MTHubTest {

	public static void main(String[] args) throws GeneralSecurityException, IOException, ClassNotFoundException {
		String uname = args[0];
		String password = args[1];
		
		KeyStore ks = KeyStoreUtils.genUserKeyStore(uname, password);
		KeyStore ts = KeyStoreUtils.genUserTrustStore("hub.public");
		
		SSLSocket s;
		ObjectOutputStream out;
		ObjectInputStream in;
		String unameRequest;
		String pwRequest;

		// Open an SSL connection to the login server and register a new user account
		s = NetworkUtils.createSecureSocket(InetAddress.getLocalHost(), Constants.HUB_PORT, ts, ks, password);
		
		System.out.println("(Registering a new user...)");
		
		out = new ObjectOutputStream(s.getOutputStream());
		out.writeObject(Constants.REGISTER);
		
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

			String regStatus = (String)in.readObject();
			System.out.println(regStatus + "\n");
		}
		
		in.close();
		out.close();
		s.close();
		
		// Now open an SSL connection to the login server and login as the user just registered
		s = NetworkUtils.createSecureSocket(InetAddress.getLocalHost(), Constants.HUB_PORT, ts, ks, password);
		
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
		
		Integer sessionSecret = 123456789;
		if(loginStatus.equals("Login Server: Welcome to P2P Chinese Checkers, " + uname + "!")) {
			sessionSecret = (Integer)in.readObject();
		}
		
		// Now make sure the logged in user can access hub services
		s = NetworkUtils.createSecureSocket(InetAddress.getLocalHost(), Constants.HUB_PORT, ts, ks, password);
		
		System.out.println("(Accessing hub services...)");
		
		out = new ObjectOutputStream(s.getOutputStream());
		out.writeObject(uname);
		
		in = new ObjectInputStream(s.getInputStream());
		String ack = (String)in.readObject();
		
		out.writeObject(sessionSecret);

		String greeting = (String)in.readObject();
		System.out.println(greeting);
		
		out.writeObject(uname + ": Hi!");
		
		String farewell = (String)in.readObject();
		System.out.println(farewell);
		
		in.close();
	    out.close();
	    s.close();
	    
	}

}
