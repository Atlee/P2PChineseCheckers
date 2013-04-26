package hub;

import java.io.IOException;
import java.io.ObjectInputStream;

import javax.net.ssl.SSLSocket;

import utils.Constants;


public class RegisterHandler extends HubHandler {

	public RegisterHandler( MultiThreadedHub hub, SSLSocket client, ObjectInputStream in) throws IOException {
		super(hub, client, in);
	}

	@Override
	public void run() {
		try {
			
			System.out.println("Hello");
			out.writeObject("Hub: Desired username, please?");

			String uname = (String)in.readObject();
			if(this.hub.pwStore.containsEntry(uname)) {
				out.writeObject(Constants.REGISTRATION_IN_USE);
			} else {
				out.writeObject(Constants.REGISTRATION_PASSWORD);
				String password = (String)in.readObject();
				if (hub.pwStore.addEntry(uname, password.toCharArray())) {
					out.writeObject(Constants.REGISTRATION_SUCCESS);
				} else {
					out.writeObject(Constants.REGISTRATION_FAILURE);
				}
			}

			client.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
