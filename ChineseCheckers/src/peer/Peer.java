package peer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;


public class Peer {

	private static final int PORT_NUM = 4321;
	private static InetAddress host = null;
	
	private static PeerProtocol p = null;
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		MyGui g = new MyGui();
		
	}
	
	public static void setPeerProtocol(String protocolID) {
		p = createProtocol(protocolID);
	}
	
	public static void executeProtocol() {
		if (p != null) {
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
	
	private static PeerProtocol createProtocol(String protocolID) {
		if (protocolID.equals("create")) {
			p = new UserRegistrationProtocol();
		} else {
			//TODO: create ClientSigningProtocol(String username)
			p = new PeerProtocol();
		}
		
		return p;
	}

}
