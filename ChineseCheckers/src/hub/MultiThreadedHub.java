package hub;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.KeyStore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;

import utils.Constants;


public class MultiThreadedHub {

	protected KeyStore keyStore;
	protected String ksFilename;
	protected String ksPassword;
	protected Lock ksLock = new ReentrantLock();
	
	protected AuthenticatedUsers online = new AuthenticatedUsers();
	
	public static void main(String[] args) {
		try {
			MultiThreadedHub hub = new MultiThreadedHub("hub.private", "hubpassword");
			hub.openHub();
		} catch(Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	public MultiThreadedHub( String keyStoreFilename, String ksPassword ) throws Exception {
		// Load the keystore
		ksLock.lock();
		this.ksFilename = keyStoreFilename;
		this.ksPassword = ksPassword;
		keyStore = KeyStore.getInstance("JKS");
		FileInputStream ksFile = new FileInputStream(this.ksFilename);
		try {
			keyStore.load(ksFile, this.ksPassword.toCharArray());
		} finally {
			ksFile.close();
			ksLock.unlock();
		}
	}
	
	protected void openHub() throws Exception {
		// Start the registration/login service
		Thread loginServerT = new Thread(new LoginServer(this));
		loginServerT.start();

		// Start the hub service
		SSLContext sslContext = SSLContext.getInstance("TLS");
	    KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
	    ksLock.lock();
	    try {
	    	kmf.init(keyStore, ksPassword.toCharArray());
	    } finally {
	    	ksLock.unlock();
	    }
        sslContext.init(kmf.getKeyManagers(), null, null);
        SSLServerSocketFactory sf = sslContext.getServerSocketFactory();
		SSLServerSocket ss = (SSLServerSocket) sf.createServerSocket(Constants.HUB_PORT);
		
		while(true) {
			SSLSocket client = (SSLSocket)ss.accept();
			
			ObjectInputStream in = new ObjectInputStream(client.getInputStream());
			String uname = (String)in.readObject();
			ObjectOutputStream out = new ObjectOutputStream(client.getOutputStream());
			out.writeObject("ACK");
			Integer secret = (Integer)in.readObject();
			if(!online.check(uname, secret)) {
				client.close();
			} else {
				out.writeObject("Hub: Welcome to the hub!");

				String reply = (String)in.readObject();
				System.out.println(reply);

				out.writeObject("Hub: Goodbye.");
			}
		}
		
	}


}
