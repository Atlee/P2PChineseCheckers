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
	
	public static final String uname = "kigobi";
	public static final String password = "asdfVALID12345";

	// If you set this to false, the test user won't login -> Hub will not allow access to the HELLO service
	public static final boolean doLogin = true;

	public static void main(String[] args) throws GeneralSecurityException, IOException, ClassNotFoundException {

		KeyStore ks = KeyStoreUtils.genUserKeyStore(uname, password);
		KeyStore ts = KeyStoreUtils.genUserTrustStore("hub.public");

		SSLSocket s;
		ObjectOutputStream out;
		ObjectInputStream in;
		String unameRequest;
		String pwRequest;
		Integer sessionSecret = 123456789;

		// Open an SSL connection to the Hub and register a new user account
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

		if(pwRequest.equals(Constants.REGISTRATION_PASSWORD)) {
			System.out.println("(Sending password...)");
			out.writeObject(password);

			String regStatus = (String)in.readObject();
			System.out.println(regStatus + "\n");
		}

		in.close();
		out.close();
		s.close();

		if(doLogin) {
			// Now open an SSL connection to the Hub and login as the user just registered
			s = NetworkUtils.createSecureSocket(InetAddress.getLocalHost(), Constants.HUB_PORT, ts, ks, password);

			System.out.println("(Attempting to login...)");

			out = new ObjectOutputStream(s.getOutputStream());
			out.writeObject(Constants.LOGIN);

			in = new ObjectInputStream(s.getInputStream());

			System.out.println("(Sending username...)");
			out.writeObject(uname);

			System.out.println("(Sending password...)");
			out.writeObject(password);

			String loginStatus = (String)in.readObject();
			System.out.println(loginStatus + "\n");

			if(loginStatus.equals(Constants.LOGIN_SUCCESS)) {
				sessionSecret = (Integer)in.readObject();
			}
		}


		for(int i=0; i < 3; i++) {
			// Now open an SSL connection to the Hub and initiate the HELLO protocol as the logged in user
			s = NetworkUtils.createSecureSocket(InetAddress.getLocalHost(), Constants.HUB_PORT, ts, ks, password);

			System.out.println("(Accessing Hub services...)");

			out = new ObjectOutputStream(s.getOutputStream());
			out.writeObject(Constants.HELLO);

			in = new ObjectInputStream(s.getInputStream());
			String ack = (String)in.readObject();
			out.writeObject(uname);

			ack = (String)in.readObject();
			out.writeObject(sessionSecret);

			String greeting = (String)in.readObject();
			System.out.println(greeting);

			out.writeObject(uname + ": Hi! (" + i + ")");

			String farewell = (String)in.readObject();
			System.out.println(farewell);

			in.close();
			out.close();
			s.close();
		}
		
	}

}
