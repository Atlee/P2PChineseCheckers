package hub;

import java.io.IOException;
import java.io.ObjectInputStream;

import javax.net.ssl.SSLSocket;


public class LoginHandler extends HubHandler {

	public LoginHandler( MultiThreadedHub hub, SSLSocket client, ObjectInputStream in) throws IOException {
		super(hub, client, in);
	}

	@Override
	public void run() {
		try {

			out.writeObject("Hub: Username?");
			String uname = (String)in.readObject();
			
			out.writeObject("Hub: Password?");
			String password = (String)in.readObject();
			
			if(hub.pwStore.authenticate(uname, password.toCharArray())) {
				Integer secret = hub.online.add(uname);
				out.writeObject("Hub: Welcome to P2P Chinese Checkers, " + uname + "!");
				out.writeObject(secret);
			} else {
				out.writeObject("Hub: Incorrect username or password. Please try again.");
			}
			
			client.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
