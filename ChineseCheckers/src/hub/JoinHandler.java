package hub;


import java.io.IOException;
import java.io.ObjectInputStream;

import javax.net.ssl.SSLSocket;

public class JoinHandler extends HubHandler {

	public JoinHandler(MultiThreadedHub hub, SSLSocket client,
			ObjectInputStream in) throws IOException {
		super(hub, client, in);
	}
	
	@Override
	public void run() {
		try {
			String uname = in.readUTF();
			if (checkCredentials(uname)) {
				
			}	
		} catch (IOException e) {
			;
		}
	}

}
