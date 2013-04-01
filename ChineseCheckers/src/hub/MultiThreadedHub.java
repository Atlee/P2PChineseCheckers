package hub;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManager;

import utils.Constants;


public class MultiThreadedHub {

	protected KeyStore keyStore;
	protected String ksFilename;
	protected String ksPassword;
	protected Lock ksLock = new ReentrantLock();
	
	protected KeyStore trustStore;
	protected String tsFilename;
	protected String tsPassword;
	protected Lock tsLock = new ReentrantLock();
	
	protected List<String> online = new ArrayList<String>();
	protected Lock onlineLock = new ReentrantLock();
	
	public static void main(String[] args) {
		try {
			MultiThreadedHub hub = new MultiThreadedHub("hub.private", "hubpassword", "all.public", "public");
			hub.openHub();
		} catch(Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	public MultiThreadedHub( String keyStoreFilename, String ksPassword, String trustStoreFilename, String tsPassword ) throws Exception {
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
		// Load the trust store
		tsLock.lock();
		this.tsFilename = trustStoreFilename;
		this.tsPassword = tsPassword;
		trustStore = KeyStore.getInstance("JKS");
		FileInputStream tsFile = new FileInputStream(this.tsFilename);
		try {
			trustStore.load(tsFile, this.tsPassword.toCharArray());
		} finally {
			tsFile.close();
			tsLock.unlock();
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
		TrustManager tm = new HubTrustManager(trustStore, tsLock);
        sslContext.init(kmf.getKeyManagers(), new TrustManager[] { tm }, null);
        SSLServerSocketFactory sf = sslContext.getServerSocketFactory();
		SSLServerSocket ss = (SSLServerSocket) sf.createServerSocket(Constants.HUB_PORT);
		ss.setNeedClientAuth(true);
		
		while(true) {
			SSLSocket client = (SSLSocket)ss.accept();
			
			ObjectOutputStream out = new ObjectOutputStream(client.getOutputStream());
			out.writeObject("Hub: Welcome to the hub!");
			
			ObjectInputStream in = new ObjectInputStream(client.getInputStream());
			String reply = (String)in.readObject();
			System.out.println(reply);
			
			out.writeObject("Hub: Goodbye.");
		}
		
	}


}
