package hub;

import java.io.IOException;
import java.io.ObjectInputStream;

import javax.net.ssl.SSLSocket;


public class RegisterHandler extends HubHandler {

	public RegisterHandler( MultiThreadedHub hub, SSLSocket client, ObjectInputStream in) throws IOException {
		super(hub, client, in);
	}

	@Override
	public void run() {
		try {
			
			out.writeObject("Hub: Desired username, please?");

			String uname = (String)in.readObject();
			if(this.hub.pwStore.containsEntry(uname)) {
				out.writeObject("Hub: That username is already in use. Please try again.\n");
			} else {
				out.writeObject("Hub: Password, please?");
				String password = (String)in.readObject();
				hub.pwStore.addEntry(uname, password.toCharArray());
				out.writeObject("Hub: Account registration successful!");
			}

			client.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
