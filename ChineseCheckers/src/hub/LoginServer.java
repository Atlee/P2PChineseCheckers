package hub;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.concurrent.locks.Lock;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import utils.Constants;


public class LoginServer implements Runnable {
	
	private KeyStore hubKeyStore;
	private Lock hksLock;
	private KeyStore hubTrustStore;
	private Lock htsLock;
	private PasswordStore pwStore;
	private List<String> online;
	private Lock onlineLock;

	public LoginServer( List<String> online , Lock onlineLock , KeyStore hubKeyStore, Lock hksLock, KeyStore hubTrustStore, Lock htsLock) {
		pwStore = new PasswordStore();
		this.online = online;
		this.onlineLock = onlineLock;
		this.hubKeyStore = hubKeyStore;
		this.hksLock = hksLock;
		this.hubTrustStore = hubTrustStore;
		this.htsLock = htsLock;
	}
	
	public void run( ) {
		
        try {
        	
        	hksLock.lock();
			SSLServerSocketFactory sf = new MySSLServerSocketFactory(hubKeyStore);
			hksLock.unlock();
			ServerSocket ss = sf.createServerSocket(Constants.LOGIN_SERVER_PORT);
			
			System.out.println("Online: " + online.toString());
			
			while(true) {
				
				Socket client = ss.accept();
				
				ObjectInputStream in = new ObjectInputStream(client.getInputStream());
				String serviceRequest = (String)in.readObject();
				
				ObjectOutputStream out = new ObjectOutputStream(client.getOutputStream());
				
				if(serviceRequest.equals("REGISTER")) {
					out.writeObject("Server: Desired username, please?");
					String uname = (String)in.readObject();
					
					if(pwStore.containsEntry(uname)) {
						out.writeObject("Server: That username is already in use. Please try again.");
					} else {
						out.writeObject("Server: Password, please?");
						String password = (String)in.readObject();
						pwStore.addEntry(uname, password.toCharArray());
						
						out.writeObject("Server: Self-signed certificate, please?");
						Certificate cert = (Certificate)in.readObject();
						htsLock.lock();
						hubTrustStore.setCertificateEntry(uname, cert);
						OutputStream tsFile = new FileOutputStream("all.public");
						hubTrustStore.store(tsFile, "public".toCharArray());
						tsFile.close();
						htsLock.unlock();
						
						out.writeObject("Server: Account registration successful!");
					}
					
				} else if(serviceRequest.equals("LOGIN")) {
					out.writeObject("Server: Username?");
					String uname = (String)in.readObject();
					out.writeObject("Server: Password?");
					String password = (String)in.readObject();
					
					if(pwStore.authenticate(uname, password.toCharArray())) {
						onlineLock.lock();
						if(!online.contains(uname)) {
							online.add(uname);
						}
						onlineLock.unlock();
						out.writeObject("Server: Welcome to P2P Chinese Checkers, " + uname + "!");
						System.out.println("Online: " + online.toString());
					} else {
						out.writeObject("Server: Incorrect username or password. Please try again.");
					}
				} else {
					out.writeObject("Server: lolwut");
				}
				
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	/* ------------------------------------------------------------------------------------------------------ */
	
	class MySSLServerSocketFactory extends SSLServerSocketFactory {
		
		private SSLContext sslContext;
		
		public MySSLServerSocketFactory( KeyStore ks ) throws GeneralSecurityException, IOException {
	        
		    KeyManagerFactory kmf = KeyManagerFactory.getInstance( "SunX509" );
			kmf.init(ks, "hubpassword".toCharArray());
			
			SecureRandom sRand = new SecureRandom();
		    sRand.nextInt();
			
			// Create a trust manager that accepts all certificates
			TrustManager tm = new X509TrustManager() {
	            public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {}

	            public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {}

	            public X509Certificate[] getAcceptedIssuers() {
	                return null;
	            }
	        };
	        
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
