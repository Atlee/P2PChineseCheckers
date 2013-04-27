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
			out.writeObject("Hub: Desired username, please?");

			String uname = (String)in.readObject();
			if (Constants.verifyUsername(uname)) {
				//if the username is allowed
				if(this.hub.pwStore.containsEntry(uname)) {
					//if the username is already in use
					out.writeObject(Constants.REGISTRATION_IN_USE);
				} else {
					out.writeObject(Constants.REGISTRATION_PASSWORD);
					String password = (String)in.readObject();
					if (Constants.verifyPassword(password.toCharArray())) {
						//if the password is allowed
						if (hub.pwStore.addEntry(uname, password.toCharArray())) {
							//if the add was successful
							out.writeObject(Constants.REGISTRATION_SUCCESS);
						} else {
							out.writeObject(Constants.REGISTRATION_FAILURE);
						}
					} else {
						out.writeObject(Constants.REGISTRATION_FAILURE);
					}
				}
			} else {
				out.writeObject(Constants.REGISTRATION_FAILURE);
			}

			client.close();
		} catch (IOException | ClassNotFoundException ex) {
			System.out.println("Error registering user");
		}
	}

}
