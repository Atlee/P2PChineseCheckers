package hub;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Set;

import javax.net.ssl.SSLSocket;

public class GetHostsProtocol extends HubProtocol {

	public GetHostsProtocol(SSLSocket client, ObjectInputStream in) throws IOException {
		super(client, in);
	}

	@Override
	public void run() {
		Set<String> hosts = Hub.getHosts();
		try {
			out.writeInt(hosts.size());
			
			for (String hostname : hosts) {
				out.writeUTF(hostname);
			}
		} catch (IOException e) {
			System.out.println("Error GetHostProtocol");
			e.printStackTrace();
		}
		
		try {
			out.close();
			in.close();
			client.close();
		} catch (IOException ex) {
			;
		}
	}
}
