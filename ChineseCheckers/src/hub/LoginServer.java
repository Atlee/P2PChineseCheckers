package hub;

import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.security.cert.Certificate;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManager;

import utils.Constants;


public class LoginServer implements Runnable {
	
	private MultiThreadedHub hub;
	private PasswordStore pwStore = new PasswordStore();

	public LoginServer( MultiThreadedHub hub ) {
		this.hub = hub;
	}
	
	public void run( ) {
		
		SSLServerSocket ss = null;
		try {
			SSLContext sslContext = SSLContext.getInstance("TLS");
			KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
			hub.ksLock.lock();
			try {
				kmf.init(hub.keyStore, hub.ksPassword.toCharArray());
			} finally {
				hub.ksLock.unlock();
			}
			TrustManager tm = new LoginServerTrustManager(hub.trustStore, hub.tsLock);
			sslContext.init(kmf.getKeyManagers(), new TrustManager[] { tm }, null);
			SSLServerSocketFactory sf = sslContext.getServerSocketFactory();
			ss = (SSLServerSocket)sf.createServerSocket(Constants.LOGIN_SERVER_PORT);
			ss.setNeedClientAuth(true);
		} catch(Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		try {

			System.out.println("Online: " + hub.online.toString());

			while(true) {

				SSLSocket client = (SSLSocket)ss.accept();
				
				ObjectInputStream in = new ObjectInputStream(client.getInputStream());
				String serviceRequest = (String)in.readObject();
				
				ObjectOutputStream out = new ObjectOutputStream(client.getOutputStream());
				
				if(serviceRequest.equals("REGISTER")) {
					out.writeObject("Login Server: Desired username, please?");
					String uname = (String)in.readObject();
					
					if(pwStore.containsEntry(uname)) {
						out.writeObject("Login Server: That username is already in use. Please try again.\n");
					} else {
						out.writeObject("Login Server: Password, please?");
						String password = (String)in.readObject();
						pwStore.addEntry(uname, password.toCharArray());
						
						out.writeObject("Login Server: Self-signed certificate, please?");
						Certificate cert = (Certificate)in.readObject();
						hub.tsLock.lock();
						OutputStream tsFile = null;
						try {
							hub.trustStore.setCertificateEntry(uname, cert);
							tsFile = new FileOutputStream("all.public");
							hub.trustStore.store(tsFile, "public".toCharArray());
						} finally {
							tsFile.close();
							hub.tsLock.unlock();
						}
						
						out.writeObject("Login Server: Account registration successful!");
					}
					
				} else if(serviceRequest.equals("LOGIN")) {
					out.writeObject("Login Server: Username?");
					String uname = (String)in.readObject();
					out.writeObject("Login Server: Password?");
					String password = (String)in.readObject();
					
					if(pwStore.authenticate(uname, password.toCharArray())) {
						hub.onlineLock.lock();
						try {
							if(!hub.online.contains(uname)) {
								hub.online.add(uname);
							}
						} finally {
							hub.onlineLock.unlock();
						}
						out.writeObject("Login Server: Welcome to P2P Chinese Checkers, " + uname + "!");
						System.out.println("Online: " + hub.online.toString());
					} else {
						out.writeObject("Login Server: Incorrect username or password. Please try again.");
					}
				} else {
					out.writeObject("Login Server: lolwut");
				}
				
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	
}
