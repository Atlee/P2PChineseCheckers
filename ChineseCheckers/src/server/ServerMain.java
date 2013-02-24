package server;

import server.HelloWorldProtocol;
import utils.EncryptUtils;
import java.net.ServerSocket;
import java.net.Socket;
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
		/*ServerSocket server = null;
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
			PublicKey clientKey = getClientKey(in, out);
			String outputLine = "Start", inputLine = null;
			byte[] fromServer;
			
			//initiate communication between the server and the client
			fromServer = EncryptUtils.encryptWithKey(outputLine, clientKey);
			sendMessage(out, fromServer);
			
			System.exit(1);
			
			//read the first input to determine what protocol to use
			inputLine = EncryptUtils.decrypt(readMessage(in));
			System.out.println("Client Protocol Request: " + inputLine);
			p = chooseProtocol(inputLine);
			fromServer = EncryptUtils.encrypt(p.processInput(inputLine, db));
			sendMessage(out, fromServer);
			
			while(!outputLine.equals("End")) {
				inputLine = EncryptUtils.decrypt(readMessage(in));
				outputLine = p.processInput(inputLine, db);
				fromServer = EncryptUtils.encrypt(outputLine);
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
		*/
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
	
	public static PublicKey getClientKey(DataInputStream in, DataOutputStream out) {
		PublicKey clientKey = null;
		String toClient = "Public", fromClient = null;
		byte[] toClientBytes = toClient.getBytes();
		sendMessage(out, toClientBytes);
		
		fromClient = EncryptUtils.decrypt(readMessage(in));
		try {
			ObjectInputStream readObj = new ObjectInputStream(
					new ByteArrayInputStream(fromClient.getBytes()));
			clientKey = (PublicKey) readObj.readObject();
		} catch (IOException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return clientKey;
	}
}
