package hub;

import java.io.IOException;
import java.io.ObjectInputStream;

import javax.net.ssl.SSLSocket;


public class HelloHandler extends HubHandler {

	public HelloHandler(MultiThreadedHub hub, SSLSocket client, ObjectInputStream in) throws IOException {
		super(hub, client, in);
	}

	@Override
	public void run() {
		try {

			out.writeObject("Hub: Username, please?");
			String uname = (String)in.readObject();
			out.writeObject("Hub: Session secret, please?");
			Integer secret = (Integer)in.readObject();
			if(!hub.online.check(uname, secret)) {
				out.writeObject("Hub: Yeah, nice try...");
				client.close();
			} else {
				out.writeObject("Hub: Welcome to the Hub!");
				String reply = (String)in.readObject();
				if(hub.verboseHub) {
					System.out.println(reply);
				}
				out.writeObject("Hub: Goodbye.");
			}
			
			client.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
