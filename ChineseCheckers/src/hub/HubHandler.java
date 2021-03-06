package hub;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javax.net.ssl.SSLSocket;


public abstract class HubHandler implements Runnable {
	
	protected MultiThreadedHub hub;
	protected SSLSocket client;
	protected ObjectOutputStream out;
	protected ObjectInputStream in;
	
	public HubHandler( MultiThreadedHub hub, SSLSocket client , ObjectInputStream in) throws IOException {
		this.hub = hub;
		this.client = client;
		this.in = in;
		this.out = new ObjectOutputStream(this.client.getOutputStream());
	}

	@Override
	public void run() {
		try {
			client.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
