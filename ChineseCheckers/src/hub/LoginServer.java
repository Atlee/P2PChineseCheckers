package hub;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;

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
			sslContext.init(kmf.getKeyManagers(), null, null);
			SSLServerSocketFactory sf = sslContext.getServerSocketFactory();
			ss = (SSLServerSocket)sf.createServerSocket(Constants.LOGIN_SERVER_PORT);
		} catch(Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		try {

			System.out.println("Online: " + hub.online.list().toString());

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
						
						out.writeObject("Login Server: Account registration successful!");
					}
					
				} else if(serviceRequest.equals("LOGIN")) {
					out.writeObject("Login Server: Username?");
					String uname = (String)in.readObject();
					out.writeObject("Login Server: Password?");
					String password = (String)in.readObject();
					
					if(pwStore.authenticate(uname, password.toCharArray())) {
						Integer secret = hub.online.add(uname);
						out.writeObject("Login Server: Welcome to P2P Chinese Checkers, " + uname + "!");
						out.writeObject(secret);
						System.out.println("Online: " + hub.online.list().toString());
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
