package client;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.net.Socket;
import java.io.Console;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

public class ClientMain {

	/**
	 * @param args
	 */
	
	private static final int PORT_NUM = 4321;
	
	public static void main(String[] args) {
		readLoginConsole();
		//listenSocket();
	}
	
	public static void listenSocket() {
		//Create socket connection
		Socket socket = null;
		PrintWriter out;
		BufferedReader in, console;
		InetAddress host = null;
		String userInput, fromServer;
		
		try{
			host = InetAddress.getLocalHost();
			
			socket = new Socket(host, PORT_NUM);
		    out = new PrintWriter(socket.getOutputStream(), true);
		    in = new BufferedReader(
		    		new InputStreamReader(socket.getInputStream()));
		    
		    console = new BufferedReader(new InputStreamReader(System.in));
		    
		    System.out.println("Please enter message to be sent");
		    while ((fromServer = in.readLine()) != null) {
		    	System.out.println("Server: " + fromServer);
		    	if (fromServer.equals("Bye")) {
		    		break;
		    	}
		    	
		    	System.out.println("Please enter next message to be sent");
		    	userInput = console.readLine();
		    	if (userInput != null) {
		    		System.out.println("Client: " + userInput);
		    		out.println(userInput);
		    	}
		    }
		    
		    out.close();
		    in.close();
		    console.close();
		    socket.close();
		} catch (UnknownHostException e) {
		    System.out.println("Unknown host: " + host);
		    System.exit(1);
		} catch  (IOException e) {
		    System.out.println("No I/O");
		    System.exit(1);
		}
	}
	
	private static String readLoginConsole() {
		Console cons;
		char[] password;
		String output = null;
		if ((cons = System.console()) != null) {
			
		} else {
			System.out.println("Console null");
		}
		
		return "";
	}
}
