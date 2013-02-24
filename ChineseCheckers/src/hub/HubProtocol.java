package hub;


import java.net.Socket;

import utils.Protocol;


public class HubProtocol extends Protocol {
	
	public void processInput(Socket s) {
		return;
	}
	
	public byte[] processInput(byte[] s) {
		return "End".getBytes();
	}
	
	
}
