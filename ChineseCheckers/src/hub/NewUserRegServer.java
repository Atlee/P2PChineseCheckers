package hub;

import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.GeneralSecurityException;
import java.util.concurrent.locks.Lock;

import utils.Constants;
import utils.NetworkUtils;

public class NewUserRegServer implements Runnable {
	
	private KeyStore truststore;
	private Lock tsLock;
	
	public NewUserRegServer( KeyStore truststore, Lock tsLock ) {
		this.truststore = truststore;
		this.tsLock = tsLock;
	}
	
	public void run() {
		
		ServerSocket ss = NetworkUtils.createServerSocket(Constants.NEW_USER_REG_PORT);
		
		while(true) {
			
			// Accept a client connection
			Socket client = null;
			try {
				client = ss.accept();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			try {
				
				// TODO: Replace with the real protocol
				ObjectOutputStream out = new ObjectOutputStream(client.getOutputStream());
				out.writeObject("Server: Desired username, please?");
				
				ObjectInputStream in = new ObjectInputStream(client.getInputStream());
				String uname = (String)in.readObject();
				
				out.writeObject("Server: Public-key certificate, please?");
				
				Certificate cert = (Certificate)in.readObject();
				System.out.println();
				//tsLock.lock();
				truststore.setCertificateEntry(uname, cert);
				OutputStream tsFile = new FileOutputStream("all-clients.public");
				truststore.store(tsFile, "public".toCharArray());
				tsFile.close();
				//tsLock.unlock();
				
				out.writeObject("Server: Thank you. Account registration successful!");
				
			} catch (IOException | ClassNotFoundException | GeneralSecurityException e) {
				e.printStackTrace();
			}
			
		}
	}
	
}
