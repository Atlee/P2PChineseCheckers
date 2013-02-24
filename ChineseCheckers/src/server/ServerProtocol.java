package server;

import java.net.Socket;
import utils.Protocol;

public class ServerProtocol extends Protocol {
	
	public void processInput(Socket s, FakeDatabase db) {
		return;
	}
	
	public byte[] processInput(byte[] s, FakeDatabase db) {
		return "End".getBytes();
	}
}
