package hub;

import java.io.IOException;
import java.io.ObjectInputStream;

import javax.net.ssl.SSLSocket;

import utils.Constants;

public class NewHostProtocol extends HubProtocol {

	public NewHostProtocol(SSLSocket client, ObjectInputStream in) throws IOException {
		super(client, in);
	}

	@Override
	public void run() {
		try {
			if (verifySession()) {
				out.writeUTF(Constants.VERIFY_SUCCESS+username);
				Hub.hostNewGame(username);
				out.writeObject(Hub.getGameKey(username));
			} else {
				out.writeUTF(Constants.VERIFY_FAILURE+username);
			}
		} catch (IOException e) {
			System.out.println("Error writing GameKey");
			//if the write fails, remove the user from the host lists
			Hub.logoutUser(username);
		}
		try {
			in.close();
			out.close();
			client.close();
		} catch (IOException ex) {
			;
		}
	}

}
