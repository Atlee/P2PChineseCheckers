package hub;

import java.io.IOException;
import java.io.ObjectInputStream;

import javax.net.ssl.SSLSocket;

import utils.Constants;


public class LoginHandler extends HubHandler {

	public LoginHandler( MultiThreadedHub hub, SSLSocket client, ObjectInputStream in) throws IOException {
		super(hub, client, in);
	}

	@Override
	public void run() {
		try {

			String uname = (String)in.readObject();
			String password = (String)in.readObject();
			
			if(hub.pwStore.authenticate(uname, password.toCharArray())) {
				Integer secret = hub.online.add(uname);
				out.writeObject(Constants.LOGIN_SUCCESS);
				out.writeObject(secret);
			} else {
				out.writeObject(Constants.LOGIN_FAILURE);
			}
			
			client.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
