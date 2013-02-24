package peer;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.security.PublicKey;



import utils.Constants;
import utils.Protocol;
import utils.SignUtils;

public class UserRegistrationProtocol extends PeerProtocol {
	
	private String userName = null;
	private String password = null;
	
	@Override
	public String processInput(String input) {
		String output = "default";
		BufferedReader stdin = new BufferedReader(
				new InputStreamReader(System.in));
		
		try {
			if (input.equals("Start")) {
				output = "create";
			} else if (input.equals("username:password")) {
				System.out.println("Please enter new username");
				output = "username\t" + stdin.readLine();
				
				System.out.println("Please enter new password");
				output += "\tpassword\t" + stdin.readLine();
			} else {
				output = "default";
			}
		} catch (IOException e) {
			System.out.println("Error reading user input");
		}
		return output;
	}
	
	public void execute(Socket s) {		
		getNewCredentials();
		
		byte[] message = userName.getBytes();
		try {
			sendProtocol(s);
			sendMessage(s, message);
			
			handleNameResponse(s);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	private void handleNameResponse(Socket s) throws IOException {
		byte[] fromServerBytes = readSignedMessage(s, getHubKey());
		String fromServer = new String(fromServerBytes);
		
		if (fromServer.equals("AVAILABLE,"+userName)) {
			//TODO: add key generation/recovery code here
			//for now assume keys in public/private.key files
			//NOTE: I think the above happens automatically in SignUtils.init()
			//sendKey(s, SignUtils.getPublic());
			sendMessage(s, userName.getBytes());
			
			fromServerBytes = readSignedMessage(s, getHubKey());
			fromServer = new String(fromServerBytes);
			if (fromServer.equals("OK,"+userName)) {
				//TODO:Save password and Username locally
				System.out.println("Username " + userName + " successfully created");
			} else {
				System.out.println("Unknown message from server");
			}			
		} else if (fromServer.equals("IN_USE,"+userName)) {
			System.out.println("Username " + userName + " is not " +
					"available, please pick another");
		} else {
			System.out.println("Unknown message from server");
		}
	}
	
	private void sendKey(Socket s, PublicKey key) throws IOException {
		ObjectOutputStream out = new ObjectOutputStream(s.getOutputStream());
		out.writeObject(key);
	}
	
	private void sendProtocol(Socket s) throws IOException {
		DataOutputStream out = new DataOutputStream(s.getOutputStream());
		out.writeInt(Constants.REGISTER);
	}
	
 	private void getNewCredentials() {
		BufferedReader stdin = new BufferedReader(
				new InputStreamReader(System.in));
		try {
			System.out.println("Please enter new username");
			userName = stdin.readLine();
		
			System.out.println("Please enter new password");
			password = stdin.readLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(1);
		}
	}
}
