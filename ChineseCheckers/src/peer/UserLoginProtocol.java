package peer;

import java.net.Socket;

import utils.MyKeyStore;
import utils.Protocol;

public class UserLoginProtocol extends Protocol {
	
	private MyKeyStore ks;
	
	public UserLoginProtocol(MyKeyStore ks) {
		this.ks = ks;
	}
}
