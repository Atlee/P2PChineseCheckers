package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;

public class Peer {

	private static final int PORT_NUM = 4321;
	private static InetAddress host = null;
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		while (true) {
			ClientProtocol p = createProtocol();
			
			Socket hub = handleCreateSocket();
			
			p.execute(hub);
		}
	}
	
	private static Socket handleCreateSocket() {
		Socket s = null;		
		try {
			host = InetAddress.getLocalHost();
			s = new Socket(host, PORT_NUM);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(1);
		}
		
		return s;
	}
	
	private static ClientProtocol createProtocol() {
		BufferedReader stdin = new BufferedReader(
				new InputStreamReader(System.in));
		String userInput = null;
		ClientProtocol p;
		
		System.out.println("Please enter username or \"create\" to create a new account");
		
		try {
			userInput = stdin.readLine();
		} catch (IOException e) {
			System.out.println("Error reading user input.");
			System.exit(1);
		}
		
		if (userInput.equals("create")) {
			p = new CreateUserProtocol();
		} else {
			//TODO: create ClientSigningProtocol(String username)
			p = new ClientProtocol();
		}
		
		return p;
	}

}
