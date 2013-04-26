package hub;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.InetAddress;
import javax.net.ssl.SSLSocket;

import utils.Constants;

public class JoinHostProtocol extends HubProtocol {

	public JoinHostProtocol(SSLSocket client, ObjectInputStream in) throws IOException {
		super(client, in);
	}

	@Override
	public void run() {
		try {
			if (verifySession()) {
				out.writeUTF(Constants.VERIFY_SUCCESS+username);
				
				String hostname = in.readUTF();
				
				InetAddress addr = Hub.getAddr(hostname);
				
				out.writeObject(addr);
				out.writeObject(Hub.getGameKey(hostname));
				
				Hub.addPlayerToGame(hostname, username);
				Hub.removeHost(hostname);
			} else {
				out.writeUTF(Constants.VERIFY_FAILURE+username);				
			}
			
			out.close();
			in.close();
			client.close();
		} catch (IOException e) {
			
		}
	}

}
