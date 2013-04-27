package hub;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.security.KeyStore;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;

import utils.Constants;


public class MultiThreadedHub {

	boolean verboseHub = true;
	
	private KeyStore keyStore;
	private char[] ksPassword;

    UserTracker online = new UserTracker();
	GameTracker games = new GameTracker();
	PasswordStore pwStore = new PasswordStore();
	StatisticsStore statStore = new StatisticsStore();

	public static void main(String[] args) throws Exception {
		MultiThreadedHub hub = new MultiThreadedHub(Constants.HUB_KS_FILENAME, Constants.HUB_KS_PASSWORD);
		hub.openHub();
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
		SSLServerSocket ss = (SSLServerSocket) sf.createServerSocket(Constants.HUB_PORT);
		
		// Start the reaper threads
		new Thread(new IdleUserReaper(this)).start();
		new Thread(new DeadGameReaper(this)).start();
		
		// Begin accepting SSL client connections...
		while(true) {
			
			if(verboseHub) {
				System.out.println("Online: " + online.listOnline().toString());
				System.out.println("Ready to accept an SSL client connection...");
			}
			
			SSLSocket client = (SSLSocket)ss.accept();
			ObjectInputStream in = new ObjectInputStream(client.getInputStream());
			
			Integer serviceRequest = (Integer)in.readObject();
			
			HubHandler handler;
			if(serviceRequest.equals(Constants.REGISTER)) {
				if(verboseHub) {
					System.out.println("    Connection accepted! Handling a REGISTER request...");
				}
				handler = new RegisterHandler(this, client, in);
			} else if(serviceRequest.equals(Constants.LOGIN)) {
				if(verboseHub) {
					System.out.println("    Connection accepted! Handling a LOGIN request...");
				}
                handler = new LoginHandler(this, client, in);			
			} else if(serviceRequest.equals(Constants.HELLO)) {
				if(verboseHub) {
					System.out.println("    Connection accepted! Handling a HELLO request...");
				}
                handler = new HelloHandler(this, client, in);
			} else {
				if(verboseHub) {
					System.out.println("    Connection accepted! But the client messed up...");
				}
				client.close();
				continue;
			}

			new Thread(handler).start();
			
		}

	}
	
}
