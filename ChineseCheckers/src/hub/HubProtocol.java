package hub;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javax.net.ssl.SSLSocket;

public abstract class HubProtocol implements Runnable {
	protected SSLSocket client;
	protected ObjectOutputStream out;
	protected ObjectInputStream in;
	protected String username;
	protected int sessionKey;
	
	public HubProtocol(SSLSocket client, ObjectInputStream in) throws IOException {
		this.client = client;
		this.out = new ObjectOutputStream(client.getOutputStream());
		this.in = in;
	}
	
	abstract public void run();
	
	public boolean verifySession() {
		boolean output = false;
		try {
			username = in.readUTF();
			sessionKey = in.readInt();
			if (Hub.verifySession(username, sessionKey)) {
				output = true;
			}
		} catch (IOException ex) {
			;
		}
		return output;
	}
}
