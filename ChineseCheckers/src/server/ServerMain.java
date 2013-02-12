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
			PrintWriter out = new PrintWriter(client.getOutputStream(), true);			
			BufferedReader in = new BufferedReader(
					new InputStreamReader(client.getInputStream()));
			String outputLine, inputLine;
			
			HelloWorldProtocol hwp = new HelloWorldProtocol();
			outputLine = hwp.processInput(null);
			out.println(outputLine);
			
			while((inputLine = in.readLine()) != null) {
				outputLine = hwp.processInput(inputLine);
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

}
