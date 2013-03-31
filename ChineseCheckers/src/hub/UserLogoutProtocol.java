package hub;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.Key;

import utils.Constants;
import utils.NetworkUtils;

public class UserLogoutProtocol implements HubProtocol {

	@Override
	public void execute(Socket s, Key sharedKey) {
		InetAddress addr = s.getInetAddress();
		Hub.removeUserLogin(addr);
	}

}
