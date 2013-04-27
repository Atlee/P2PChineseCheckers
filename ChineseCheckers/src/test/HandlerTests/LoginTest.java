package test.HandlerTests;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.security.KeyStore;

import javax.net.ssl.SSLSocket;

import utils.Constants;
import utils.KeyStoreUtils;
import utils.NetworkUtils;

public class LoginTest extends HubProtocolTest {

	@Override
	public void test() throws Exception {
		//assumes users in RegisterTest are registered with the hub
		RegisterTest rt = new RegisterTest();
		rt.test();
		
		//*****************************************NEXT TEST******************************
		//Test that each of the registered valid usernames can now login
		for (String name : rt.valid_unames) {
			KeyStore ks = KeyStoreUtils.genUserKeyStore(name, "pw");
			KeyStore ts = KeyStoreUtils.genUserTrustStore("hub.public");

			SSLSocket s;
			ObjectOutputStream out;
			ObjectInputStream in;
			Integer sessionSecret = -1;
			
			// Now open an SSL connection to the Hub and login as the user just registered
			s = NetworkUtils.createSecureSocket(InetAddress.getLocalHost(), Constants.HUB_PORT, ts, ks, "pw");

			System.out.println("(Attempting to login...)");

			out = new ObjectOutputStream(s.getOutputStream());
			out.writeObject(Constants.LOGIN);

			in = new ObjectInputStream(s.getInputStream());

			System.out.println("(Sending username...)");
			out.writeObject(name);

			System.out.println("(Sending password...)");
			out.writeObject(rt.passphrase);

			String loginStatus = (String)in.readObject();
			System.out.println(loginStatus + "\n");

			if(loginStatus.equals(Constants.LOGIN_SUCCESS)) {
				sessionSecret = (Integer)in.readObject();
				if (sessionSecret == -1) {
					//make sure the secret is different
					throw new Exception();
				}
			} else {
				//make sure the login is accepted
				throw new Exception();
			}
		}
		
		//*****************************************NEXT TEST******************************
		//test that an unregistered name/pw cannot login
		try {
			String name = "NotYetXRegisteredJKLUKKSJFLLUEKLJFLAIEJF";
			KeyStore ks = KeyStoreUtils.genUserKeyStore(name, "pw");
			KeyStore ts = KeyStoreUtils.genUserTrustStore("hub.public");

			SSLSocket s;
			ObjectOutputStream out;
			ObjectInputStream in;
			Integer sessionSecret = -1;
			
			// Now open an SSL connection to the Hub and login as the user just registered
			s = NetworkUtils.createSecureSocket(InetAddress.getLocalHost(), Constants.HUB_PORT, ts, ks, "pw");

			System.out.println("(Attempting to login...)");

			out = new ObjectOutputStream(s.getOutputStream());
			out.writeObject(Constants.LOGIN);

			in = new ObjectInputStream(s.getInputStream());

			System.out.println("(Sending username...)");
			out.writeObject(name);

			System.out.println("(Sending password...)");
			out.writeObject(rt.passphrase);

			String loginStatus = (String)in.readObject();
			System.out.println(loginStatus + "\n");

			if(loginStatus.equals(Constants.LOGIN_SUCCESS)) {
				sessionSecret = (Integer)in.readObject();
				if (sessionSecret == -1) {
					//make sure the secret is different
					throw new Exception();
				}
			} else {
				//make sure the login is accepted
				throw new Exception();
			}
		} finally {
			
		}
	}
	
}
