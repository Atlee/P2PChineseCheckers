package test.HandlerTests;

import java.io.File;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.security.KeyStore;

import javax.net.ssl.SSLSocket;

import utils.Constants;
import utils.KeyStoreUtils;
import utils.NetworkUtils;

/** This class tests the RegisterHandler functionality on the Hub.  It tries sending
 * many different passwords and usernames that may or may not be allowed, and will
 * throw an exception if it receives an inappropriate response. 
 * 
 * It also tests sending a duplicate username, and finally a message that the hub isn't
 * expecting.
 * 
 * @author Atlee
 *
 */

public class RegisterTest extends HubProtocolTest {
	
	//there are no restrictions here on passwords so check that these are not
	//allowed
	String[] invalid_unames = {
			" LeadingSpace",
			"TrailingTab\t",
			"Space In MIddle",
			"TooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooL000000000000000000000000000000000000000000000000000000000000000000000000000NG1234",
	};
	public String[] valid_unames = {
			"C0nt41n5NUMB3r5",
			"xxxLeetH4x0rxxx420xxxBLAZEITxxxYOLOxxxSWAG",
			"No_Numbers"
	};
	public String passphrase = "BasicPW123456789";
	String[] valid_pws = {
			"ValidPassword1234",
			"NoNumbers;",
	};
	String[] invalid_pws = {
			" InvalidFrontSpace3422",
			"Invalid midSpace1234",
			"TooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooL000000000000000000000000000000000000000000000000000000000000000000000000000NG1234",				
	};
	
	public static void main(String[] argv) throws Exception {
		RegisterTest rt = new RegisterTest();
		rt.test();
	}

	@Override
	public void test() throws Exception {
		File f = new File("password.txt");
		if (f.exists()) {
			f.delete();
		}
		
		//*****************************************NEXT TEST******************************
		
		//test invalid usernames
		for (String uname : invalid_unames) {
			KeyStore ks = KeyStoreUtils.genUserKeyStore(uname, "pw");
			KeyStore ts = KeyStoreUtils.genUserTrustStore("hub.public");

			// Open an SSL connection to the Hub and register a new user account
			SSLSocket s = NetworkUtils.createSecureSocket(InetAddress.getLocalHost(), Constants.HUB_PORT, ts, ks, "pw");
			ObjectOutputStream out;
			ObjectInputStream in;
			
			out = new ObjectOutputStream(s.getOutputStream());
			out.writeObject(Constants.REGISTER);
			String unameRequest;
			String pwRequest;

			in = new ObjectInputStream(s.getInputStream());
			unameRequest = (String)in.readObject();
			System.out.println(unameRequest);

			out.writeObject(uname);

			pwRequest = (String)in.readObject();
			System.out.println(pwRequest);

			if(pwRequest.equals(Constants.REGISTRATION_PASSWORD)) {
				out.writeObject(passphrase);

				String regStatus = (String)in.readObject();
				if (regStatus.equals(Constants.REGISTRATION_SUCCESS)) {
					throw new Exception();
				} else {
					throw new Exception();
				}
			} else if (pwRequest.equals(Constants.REGISTRATION_IN_USE)) {
				throw new Exception();
			} else {
				//we are testing invalid usernames, therefore this is a success;
			}
			
			in.close();
			out.close();
			s.close();
		}
		
		//*****************************************NEXT TEST******************************
		
		//test valid usernames
		for (String uname : valid_unames) {
			KeyStore ks = KeyStoreUtils.genUserKeyStore(uname, "pw");
			KeyStore ts = KeyStoreUtils.genUserTrustStore("hub.public");

			// Open an SSL connection to the Hub and register a new user account
			SSLSocket s = NetworkUtils.createSecureSocket(InetAddress.getLocalHost(), Constants.HUB_PORT, ts, ks, "pw");
			ObjectOutputStream out;
			ObjectInputStream in;
			
			out = new ObjectOutputStream(s.getOutputStream());
			out.writeObject(Constants.REGISTER);
			String unameRequest;
			String pwRequest;

			in = new ObjectInputStream(s.getInputStream());
			unameRequest = (String)in.readObject();
			System.out.println(unameRequest);

			out.writeObject(uname);

			pwRequest = (String)in.readObject();
			System.out.println(pwRequest);

			if(pwRequest.equals(Constants.REGISTRATION_PASSWORD)) {
				out.writeObject(passphrase);

				String regStatus = (String)in.readObject();
				if (regStatus.equals(Constants.REGISTRATION_SUCCESS)) {
					//these should successfully register;
				} else {
					throw new Exception();
				}
			} else if (pwRequest.equals(Constants.REGISTRATION_IN_USE)) {
				throw new Exception();
			} else {
				throw new Exception();
			}
			
			in.close();
			out.close();
			s.close();
		}
		
		//*****************************************NEXT TEST******************************
		try {
			//now that all the valid usernames have been used up, retry sending one of them
			//to make sure that duplicates are rejected.
			KeyStore ks = KeyStoreUtils.genUserKeyStore(valid_unames[0], "pw");
			KeyStore ts = KeyStoreUtils.genUserTrustStore("hub.public");
	
			// Open an SSL connection to the Hub and register a new user account
			SSLSocket s = NetworkUtils.createSecureSocket(InetAddress.getLocalHost(), Constants.HUB_PORT, ts, ks, "pw");
			ObjectOutputStream out;
			ObjectInputStream in;
			
			out = new ObjectOutputStream(s.getOutputStream());
			out.writeObject(Constants.REGISTER);
			String unameRequest;
			String pwRequest;
	
			in = new ObjectInputStream(s.getInputStream());
			unameRequest = (String)in.readObject();
			System.out.println(unameRequest);
	
			out.writeObject(valid_unames[0]);
	
			pwRequest = (String)in.readObject();
			System.out.println(pwRequest);
	
			if(pwRequest.equals(Constants.REGISTRATION_PASSWORD)) {
				out.writeObject(passphrase);
	
				String regStatus = (String)in.readObject();
				if (regStatus.equals(Constants.REGISTRATION_SUCCESS)) {
					throw new Exception();
				} else {
					throw new Exception();
				}
			} else if (pwRequest.equals(Constants.REGISTRATION_IN_USE)) {
				//this should be a duplicate
			} else {
				throw new Exception();
			}
			
			in.close();
			out.close();
			s.close();
		} catch (Exception e) {
			throw e;
		}
		
		//*****************************************NEXT TEST******************************
		
		//test invalid passwords
		for (String pw : invalid_pws) {
			KeyStore ks = KeyStoreUtils.genUserKeyStore("ValidName", "pw");
			KeyStore ts = KeyStoreUtils.genUserTrustStore("hub.public");

			// Open an SSL connection to the Hub and register a new user account
			SSLSocket s = NetworkUtils.createSecureSocket(InetAddress.getLocalHost(), Constants.HUB_PORT, ts, ks, "pw");
			ObjectOutputStream out;
			ObjectInputStream in;
			
			out = new ObjectOutputStream(s.getOutputStream());
			out.writeObject(Constants.REGISTER);
			String unameRequest;
			String pwRequest;

			in = new ObjectInputStream(s.getInputStream());
			unameRequest = (String)in.readObject();
			System.out.println(unameRequest);

			out.writeObject("ValidName");

			pwRequest = (String)in.readObject();
			System.out.println(pwRequest);

			if(pwRequest.equals(Constants.REGISTRATION_PASSWORD)) {
				out.writeObject(pw);

				String regStatus = (String)in.readObject();
				if (regStatus.equals(Constants.REGISTRATION_SUCCESS)) {
					throw new Exception();
				} else {
					//these should not successfully register;
				}
			} else if (pwRequest.equals(Constants.REGISTRATION_IN_USE)) {
				throw new Exception();
			} else {
				throw new Exception();
			}
			
			in.close();
			out.close();
			s.close();
		}
		
		//*****************************************NEXT TEST******************************
		
		//test valid passwords
		int cnt = 0;
		for (String pw : valid_pws) {
			KeyStore ks = KeyStoreUtils.genUserKeyStore("ValidName", "pw");
			KeyStore ts = KeyStoreUtils.genUserTrustStore("hub.public");

			// Open an SSL connection to the Hub and register a new user account
			SSLSocket s = NetworkUtils.createSecureSocket(InetAddress.getLocalHost(), Constants.HUB_PORT, ts, ks, "pw");
			ObjectOutputStream out;
			ObjectInputStream in;
			
			out = new ObjectOutputStream(s.getOutputStream());
			out.writeObject(Constants.REGISTER);
			String unameRequest;
			String pwRequest;

			in = new ObjectInputStream(s.getInputStream());
			unameRequest = (String)in.readObject();
			System.out.println(unameRequest);

			out.writeObject("ValidName"+cnt);

			pwRequest = (String)in.readObject();
			System.out.println(pwRequest);

			if(pwRequest.equals(Constants.REGISTRATION_PASSWORD)) {
				out.writeObject(pw);

				String regStatus = (String)in.readObject();
				if (regStatus.equals(Constants.REGISTRATION_SUCCESS)) {
					//these should successfully register;
				} else {
					throw new Exception();
				}
			} else if (pwRequest.equals(Constants.REGISTRATION_IN_USE)) {
				throw new Exception();
			} else {
				throw new Exception();
			}
			cnt++;
			
			in.close();
			out.close();
			s.close();
		}
		
		//*****************************************NEXT TEST******************************
		//demonstrate graceful error handling
		try {
			KeyStore ks = KeyStoreUtils.genUserKeyStore("ValidName", "pw");
			KeyStore ts = KeyStoreUtils.genUserTrustStore("hub.public");
	
			// Open an SSL connection to the Hub and register a new user account
			SSLSocket s = NetworkUtils.createSecureSocket(InetAddress.getLocalHost(), Constants.HUB_PORT, ts, ks, "pw");
			ObjectOutputStream out;
			ObjectInputStream in;
			
			out = new ObjectOutputStream(s.getOutputStream());
			out.writeObject(Constants.REGISTER);
			
			out.writeObject("FINAL_OBJECT");
		} finally {
			System.out.println("Test Over");
		}
	}

}
