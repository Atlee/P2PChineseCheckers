package hub;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


import utils.Constants;


public class Hub {
	
	private static final int PORT_NUM = Constants.PORT_NUM;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ServerSocket server = handleCreateServerSocket();
		//FakeDatabase db = new FakeDatabase();
		//db.init();
		
		while (true) {
			//waits for a client to connect before proceeding
			Socket client = handleCreateSocket(server);
			HubProtocol p = getProtocol(client);
			p.processInput(client);
			closeSocket(client);
		}
	}
	
	private static void closeSocket(Socket s) {
		try {
			s.getOutputStream().close();
			s.close();
		} catch (IOException e) {
			System.out.println("Error closing socket");
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	private static HubProtocol getProtocol(Socket s) {
		DataInputStream in = null;
		int id = -1;
		HubProtocol p = null;
		
		try {
			in = new DataInputStream(s.getInputStream());
			id = in.readInt();
		} catch (IOException e) {
			System.out.println("Error determining protocol ID");
			e.printStackTrace();
			System.exit(1);
		}
		
		//TODO: add login protocol
		switch (id) {
		case Constants.REGISTER:
			p = new UserRegistrationProtocol();
			break;
		default:
			p = new HubProtocol();
		}
		return p;
	}
	
	private static ServerSocket handleCreateServerSocket() {
		//establish port for server
		ServerSocket server = null;
		try {
			server = new ServerSocket(PORT_NUM);
		} catch (IOException e) {
			System.out.println("Could not listen on port " + PORT_NUM);
			e.printStackTrace();
			System.exit(-1);
		}
		return server;
	}
	
	private static Socket handleCreateSocket(ServerSocket server) {
		Socket client = null;
		//accept a connecting client
		try {
			client = server.accept();
		} catch (IOException e) {
			System.out.println("Accept failed: " + PORT_NUM);
			e.printStackTrace();
			System.exit(-1);
		}
		return client;
	}
}
