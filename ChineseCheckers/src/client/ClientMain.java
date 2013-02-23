package client;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.net.Socket;
import java.nio.CharBuffer;
import java.io.ByteArrayOutputStream;
import java.io.Console;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import utils.EncryptUtils;

public class ClientMain {

	/**
	 * @param args
	 */
	
	private static final int PORT_NUM = 4321;
	private static final int BUFFER_SIZE = 1024;
	
	public static void main(String[] args) {
		ClientProtocol p = createProtocol();
		listenSocket(p);
	}
	
	public static void listenSocket(ClientProtocol p) {
		//Create socket connection
		Socket socket = null;
		BufferedReader console;
		DataInputStream in;
		DataOutputStream out;
		InetAddress host = null;
		String userInput, frmServerString;
		byte[] fromServer;
		
		try{
			host = InetAddress.getLocalHost();
			
			socket = new Socket(host, PORT_NUM);
		    out = new DataOutputStream(socket.getOutputStream());
		    in = new DataInputStream(socket.getInputStream());
		    
		    console = new BufferedReader(new InputStreamReader(System.in));
		    
		    while ((fromServer = readMessage(in)) != null) {
		    	frmServerString = EncryptUtils.getKeyDecrypt(fromServer);
		    	System.out.println("Server: " + frmServerString);
		    	
		    	if (frmServerString.equals("End")) {
		    		break;
		    	}
		    	
		    	userInput = p.processInput(frmServerString);
		    	if (userInput != null) {
		    		System.out.println("Client: " + userInput);
		    		byte[] cipherText = EncryptUtils.getKeyEncrypt(userInput);
		    		sendMessage(out, cipherText);
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
		}
		
		if (userInput.equals("create")) {
			p = new CreateUserProtocol();
		} else {
			p = new ClientProtocol();
		}
		
		return p;
	}
}
