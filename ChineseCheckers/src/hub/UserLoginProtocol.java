package hub;

import java.net.Socket;
import java.security.KeyStore;

import utils.MyKeyStore;
import utils.Protocol;

public class UserLoginProtocol extends Protocol {
	
	private MyKeyStore ks;

	public UserLoginProtocol(MyKeyStore ks) {
		// TODO Auto-generated constructor stub
		this.ks = ks;
	}

	@Override
	public void execute(Socket s) {
		// TODO Auto-generated method stub
		
	}

}
