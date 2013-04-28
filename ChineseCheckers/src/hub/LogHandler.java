package hub;

import java.io.IOException;
import java.io.ObjectInputStream;

import javax.net.ssl.SSLSocket;


public class LogHandler extends HubHandler {

	public LogHandler(MultiThreadedHub hub, SSLSocket client, ObjectInputStream in) throws IOException {
		super(hub, client, in);
	}
	
	@Override
	public void run() {
		
	}

}
