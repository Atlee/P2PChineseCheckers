package hub;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.KeyStore;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;

import hub.HubConstants;


public class MultiThreadedHub {

	private KeyStore keyStore;
	private char[] ksPassword;

	private PasswordStore pwStore = new PasswordStore();
	private OnlineUsers online = new OnlineUsers();

	public static void main(String[] args) {
		try {
			MultiThreadedHub hub = new MultiThreadedHub(HubConstants.KS_FILENAME, HubConstants.KS_PASSWORD);
			hub.openHub();
		} catch(Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	public MultiThreadedHub( String keyStoreFilename, String ksPassword ) throws Exception {
		// Load the keystore containing the Hub's private key
		this.ksPassword = ksPassword.toCharArray();
		keyStore = KeyStore.getInstance("JKS");
		FileInputStream ksFile = new FileInputStream(keyStoreFilename);
		try {
			keyStore.load(ksFile, this.ksPassword);
		} finally {
			ksFile.close();
		}
	}

	protected void openHub() throws Exception {
		// Open an SSL server socket on HUB_PORT
		SSLContext sslContext = SSLContext.getInstance("TLS");
		KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
		kmf.init(keyStore, ksPassword);
		sslContext.init(kmf.getKeyManagers(), null, null);
		SSLServerSocketFactory sf = sslContext.getServerSocketFactory();
		SSLServerSocket ss = (SSLServerSocket) sf.createServerSocket(HubConstants.HUB_PORT);

		// Begin accepting SSL client connections
		while(true) {
			SSLSocket client = (SSLSocket)ss.accept();
			ObjectOutputStream out = new ObjectOutputStream(client.getOutputStream());
			ObjectInputStream in = new ObjectInputStream(client.getInputStream());

			String serviceRequest = (String)in.readObject();
			
			if(serviceRequest.equals("REGISTER")) {
				out.writeObject("Hub: Desired username, please?");
				String uname = (String)in.readObject();
				if(pwStore.containsEntry(uname)) {
					out.writeObject("Hub: That username is already in use. Please try again.\n");
				} else {
					out.writeObject("Hub: Password, please?");
					String password = (String)in.readObject();
					pwStore.addEntry(uname, password.toCharArray());
					out.writeObject("Hub: Account registration successful!");
				}
				
			} else if(serviceRequest.equals("LOGIN")) {
				out.writeObject("Hub: Username?");
				String uname = (String)in.readObject();
				out.writeObject("Hub: Password?");
				String password = (String)in.readObject();
				if(pwStore.authenticate(uname, password.toCharArray())) {
					Integer secret = online.add(uname);
					out.writeObject("Hub: Welcome to P2P Chinese Checkers, " + uname + "!");
					out.writeObject(secret);
				} else {
					out.writeObject("Hub: Incorrect username or password. Please try again.");
				}
				
			} else {
				String uname = (String)in.readObject();
				out.writeObject("ACK");
				Integer secret = (Integer)in.readObject();
				if(!online.check(uname, secret)) {
					out.writeObject("Hub: lolwut");
					client.close();
				} else {
					out.writeObject("Hub: Welcome to the Hub!");
					String reply = (String)in.readObject();
					System.out.println(reply);
					out.writeObject("Hub: Goodbye.");
				}
			}
			
		}

	}
	
}
