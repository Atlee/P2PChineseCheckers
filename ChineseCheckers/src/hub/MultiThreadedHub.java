package hub;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.net.ssl.SSLServerSocket;

import utils.Constants;
import utils.KeyStoreUtils;
import utils.NetworkUtils;

public class MultiThreadedHub {

	private static KeyStore keystore;
	private static Lock ksLock = new ReentrantLock();
	
	private static KeyStore truststore;
	private static Lock tsLock = new ReentrantLock();
	
	public static void main(String[] args) throws GeneralSecurityException, IOException, ClassNotFoundException {
		//ksLock.lock();
		keystore = KeyStoreUtils.loadHubKeyStore("hub.private", "hubpassword");
		//ksLock.unlock();
		//tsLock.lock();
		truststore = KeyStoreUtils.loadHubTrustStore("all-clients.public");
		//tsLock.unlock();
		
		// Start the non-SSL new user registration server
		Thread newUserRegT = new Thread(new NewUserRegServer(truststore, tsLock));
		newUserRegT.start();
		
		//ksLock.lock();
		//tsLock.lock();
		SSLServerSocket ss = NetworkUtils.createSecureServerSocket(Constants.HUB_SSL_PORT, truststore, keystore, "hubpassword");
		//tsLock.unlock();
		//ksLock.unlock();
		
		while(true) {
			
			//ksLock.lock();
			//tsLock.lock();
			Socket client = ss.accept();
			//tsLock.unlock();
			//ksLock.unlock();
			
			// TODO: implement an SSLConnectionHandler class (that implements Runnable) to process 
			// client connections properly and IN PARALLEL
			
			ObjectOutputStream out = new ObjectOutputStream(client.getOutputStream());
			out.writeObject("SSL Server: Hello!");
			
			ObjectInputStream in = new ObjectInputStream(client.getInputStream());
			String reply = (String)in.readObject();
			System.out.println(reply);
			
			out.writeObject("SSL Server: Goodbye.");
			
		}
		
	}

}
