package hub;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import sun.security.x509.X509CertImpl;
import sun.security.x509.X509CertInfo;
import utils.Constants;


public class MultiThreadedHub {

	private KeyStore keystore;
	private Lock ksLock = new ReentrantLock();
	
	private KeyStore truststore;
	private Lock tsLock = new ReentrantLock();
	
	private List<String> online = new ArrayList<String>();
	private Lock onlineLock = new ReentrantLock();
	
	public static void main(String[] args) throws GeneralSecurityException, IOException, ClassNotFoundException {
		MultiThreadedHub hub = new MultiThreadedHub("hub.private", "hubpassword", "all.public", "public");
		hub.openHub();
	}
	
	public MultiThreadedHub( String keyStoreFile, String ksPassword, String trustStoreFile, String tsPassword ) throws GeneralSecurityException, IOException {
		// Load the keystore
		ksLock.lock();
		keystore = KeyStore.getInstance("JKS");
		FileInputStream ksFile = new FileInputStream(keyStoreFile);
		keystore.load(ksFile, ksPassword.toCharArray());
		ksFile.close();
		ksLock.unlock();
		// Load the trust store
		tsLock.lock();
		truststore = KeyStore.getInstance("JKS");
		FileInputStream tsFile = new FileInputStream(trustStoreFile);
		truststore.load(tsFile, "public".toCharArray());
		tsFile.close();
		tsLock.unlock();
	}
	
	public void openHub() throws GeneralSecurityException, IOException, ClassNotFoundException {
		// Start the registration/login service
		Thread loginServerT = new Thread(new LoginServer(online, onlineLock, keystore, ksLock, truststore, tsLock));
		loginServerT.start();

		// Start the hub service
		SSLServerSocketFactory sf = new MySSLServerSocketFactory(online, onlineLock, keystore, ksLock, truststore, tsLock);
		ServerSocket ss = sf.createServerSocket(Constants.HUB_PORT);
		
		while(true) {

			Socket client = ss.accept();
			
			//TODO: implement a runnable connection handler, so we can spawn new threads for parallel connections
			
			ObjectOutputStream out = new ObjectOutputStream(client.getOutputStream());
			out.writeObject("Hub: Welcome!");
			
			ObjectInputStream in = new ObjectInputStream(client.getInputStream());
			String reply = (String)in.readObject();
			System.out.println(reply);
			
			out.writeObject("Hub: Goodbye.");
			
		}
		
	}
	
    /* ------------------------------------------------------------------------------------------------------ */
	
	class MySSLServerSocketFactory extends SSLServerSocketFactory {
		
		private SSLContext sslContext;
		
		public MySSLServerSocketFactory( List<String> online, Lock onlineLock, KeyStore ks , Lock ksLock, KeyStore ts, Lock tsLock ) throws GeneralSecurityException, IOException {
		    KeyManagerFactory kmf = KeyManagerFactory.getInstance( "SunX509" );
			kmf.init(ks, "hubpassword".toCharArray());
			
			SecureRandom sRand = new SecureRandom();
		    sRand.nextInt();
			
			// Get the hub's custom trust manager
			TrustManager tm = new HubTrustManager(ts, tsLock, online, onlineLock);
	        
			sslContext = SSLContext.getInstance("TLS");
	        sslContext.init(kmf.getKeyManagers(), new TrustManager[] { tm }, sRand);
		}

		@Override
		public String[] getDefaultCipherSuites() {
			return sslContext.getServerSocketFactory().getDefaultCipherSuites();
		}

		@Override
		public String[] getSupportedCipherSuites() {
			return sslContext.getServerSocketFactory().getSupportedCipherSuites();
		}

		@Override
		public ServerSocket createServerSocket(int arg0) throws IOException {
			return sslContext.getServerSocketFactory().createServerSocket(arg0);
		}

		@Override
		public ServerSocket createServerSocket(int arg0, int arg1) throws IOException {
			return sslContext.getServerSocketFactory().createServerSocket(arg0, arg1);
		}

		@Override
		public ServerSocket createServerSocket(int arg0, int arg1, InetAddress arg2) throws IOException {
			return sslContext.getServerSocketFactory().createServerSocket(arg0, arg1, arg2);
		}
	
	}

}
