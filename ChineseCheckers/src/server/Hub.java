package server;

import server.HelloWorldProtocol;
import utils.EncryptUtils;
import utils.Protocol;

import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketImpl;
import java.security.PublicKey;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.InputStreamReader;


public class Hub {
	
	//TODO: move to a constants class in utility?
	private static final int PORT_NUM = 4321;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ServerSocket server = handleCreateServerSocket();
		FakeDatabase db = new FakeDatabase();
		//db.init();
		
		while (true) {
			//waits for a client to connect before proceeding
			Socket client = handleCreateSocket(server);
			ServerProtocol p = getProtocol(client);
			p.processInput(client, db);
			closeSocket(client);
		}
	}
	
	private static void closeSocket(Socket s) {
		try {
			s.getOutputStream().close();
			s.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	private static ServerProtocol getProtocol(Socket s) {
		DataInputStream in = null;
		int id = -1;	
		ServerProtocol p = null;
		
		try {
			in = new DataInputStream(s.getInputStream());
			id = in.readInt();
		} catch (IOException e) {
			System.out.println("Error Reading protocol");
			e.printStackTrace();
			System.exit(1);
		}
		
		//TODO: add login protocol
		switch (id) {
		case Protocol.CREATE:
			p = new ServerCreateUserProtocol();
			break;
		default:
			p = new ServerProtocol();
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
