package server;

import server.HelloWorldProtocol;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.InputStreamReader;

public class ServerMain {
	
	private static final int PORT_NUM = 4321;
	
	public static final boolean DEBUG = true;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		listenSocketSingle();
	}
	
	private static void listenSocketSingle() {
		ServerSocket server = null;
		Socket client = null;
		
		//establish port for server
		try {
			server = new ServerSocket(PORT_NUM);
		} catch (IOException e) {
			System.out.println("Could not listen on port " + PORT_NUM);
			System.exit(-1);
		}
		
		//accept a connecting client
		try {
			client = server.accept();
		} catch (IOException e) {
			System.out.println("Accept failed: " + PORT_NUM);
			System.exit(-1);
		}
		
		//get information from the client
		try{
			FakeDatabase db = new FakeDatabase();
			db.init();
			ServerProtocol p = null;
			PrintWriter out = new PrintWriter(client.getOutputStream(), true);			
			BufferedReader in = new BufferedReader(
					new InputStreamReader(client.getInputStream()));
			String outputLine, inputLine;
			
			//initiate communication between the server and the client
			out.println("");
			
			//read the first input to determine what protocol to use
			inputLine = in.readLine();
			p = chooseProtocol(inputLine);
			out.println(p.processInput(inputLine, db));
			
			while((inputLine = in.readLine()) != null) {
				outputLine = p.processInput(inputLine, db);
				out.println(outputLine);
			}
			
			out.close();
			in.close();
			client.close();
			server.close();
		} catch (IOException e) {
			System.out.println("Read Failed");
			System.exit(-1);
		}
	}
	
	public static ServerProtocol chooseProtocol(String input) {
		ServerProtocol p;
		if (input.equals("create")) {
			p = new ServerCreateUserProtocol();
		} else {
			p = new ServerProtocol();
		}
		return p;
	}
}
