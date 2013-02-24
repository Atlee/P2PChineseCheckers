package protocols;


import java.net.Socket;


public class ServerProtocol extends Protocol {
	
	public void processInput(Socket s) {
		return;
	}
	
	public byte[] processInput(byte[] s) {
		return "End".getBytes();
	}
	
	
}
