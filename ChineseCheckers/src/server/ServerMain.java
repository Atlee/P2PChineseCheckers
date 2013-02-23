package server;

import server.HelloWorldProtocol;
import utils.EncryptUtils;

import java.net.ServerSocket;
import java.net.Socket;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.InputStreamReader;

public class ServerMain {
	
	private static final int PORT_NUM = 4321;
	
	public static final boolean DEBUG = true;
	
	public static final int BUFFER_SIZE = 1024;

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
			DataOutputStream out = new DataOutputStream(client.getOutputStream());			
			DataInputStream in = new DataInputStream(client.getInputStream());
			String outputLine = "Start", inputLine = null;
			byte[] fromServer;
			
			//initiate communication between the server and the client
			fromServer = EncryptUtils.getKeyEncrypt(outputLine);
			sendMessage(out, fromServer);
			
			//read the first input to determine what protocol to use
			inputLine = EncryptUtils.getKeyDecrypt(readMessage(in));
			System.out.println("Client Protocol Request: " + inputLine);
			p = chooseProtocol(inputLine);
			fromServer = EncryptUtils.getKeyEncrypt(p.processInput(inputLine, db));
			sendMessage(out, fromServer);
			
			while(!outputLine.equals("End")) {
				inputLine = EncryptUtils.getKeyDecrypt(readMessage(in));
				outputLine = p.processInput(inputLine, db);
				fromServer = EncryptUtils.getKeyEncrypt(outputLine);
				sendMessage(out, fromServer);
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
	
	private static byte[] readMessage(DataInputStream in) {
		byte[] buffer = null;
		try {
			int length = in.readInt();
			buffer = new byte[length];
			in.read(buffer, 0, length);
		} catch (IOException e) {
			System.out.println("Error processing input from Socket");
			System.exit(1);
		}
		return buffer;
	}
	
	private static void sendMessage(DataOutputStream out, byte[] message) {
		try {
			out.writeInt(message.length);
			out.write(message);
		} catch (IOException e) {
			System.out.println("Error writing to socket");
			System.exit(1);
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
