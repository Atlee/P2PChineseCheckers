package hub;


import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.security.PublicKey;



public class UserRegistrationProtocol extends HubProtocol {
	
	private String newUser = null;
	private PublicKey clientPublicKey = null;
	
	public void processInput(Socket s) {
		try {
			boolean success = handleGetUser(s);
			if (!success) {
				//close the socket -- the user opens a new line of communication
				//to try again
				return;
			}
		
			handleGetKey(s);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	private void handleGetKey(Socket s) throws IOException {
		try {
			ObjectInputStream in = new ObjectInputStream(s.getInputStream());
			clientPublicKey = (PublicKey) in.readObject();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(1);
		}
		
		byte[] message = readSignedMessage(s, clientPublicKey);
		String messageString = new String(message);
		if (message != null && messageString.equals(newUser)) {
			//db.setValue(newUser, clientPublicKey);
			byte[] toClient = ("OK,"+newUser).getBytes();
			//sendSignedMessage(s, toClient);
			sendMessage(s, toClient);
		}
	}
	
	private boolean handleGetUser(Socket s) throws IOException {
		//this read should be the prospective username
		byte[] fromClientBytes = readMessage(s);
		newUser = new String(fromClientBytes);
		//TODO:check if newUser is in the database in the if condition
		if (false) {
			//if the key is already in use
			byte[] response = ("IN_USE," + newUser).getBytes();
			//sendSignedMessage(s, response);
			sendMessage(s, response);
			return false;
		} else {
			byte[] response = ("AVAILABLE,"+newUser).getBytes();
			//sendSignedMessage(s, response);
			sendMessage(s, response);
			return true;
		}
	}
}


/*
 * @Override
	public String processInput(String s, FakeDatabase db) {
		String message;
		if (s == null) {
			message = "";
		} else if (s.equals("create")) {
			message = "username:password";
		} else {
			Scanner scn = new Scanner(s);
			String op;
			String username = null;
			String password = null;
			message = "Retry";
			scn.useDelimiter("\t");
			op = scn.next();
			if (op.equals("username")) {
				username = scn.next();
				op = scn.next();
				if (op.equals("password")) {
					password = scn.next();
					db.setValue(username, password);
				}
				message = "End";
			}
			System.out.println(username + ":" + password);
			scn.close();
		}
		return message;
	}
*/