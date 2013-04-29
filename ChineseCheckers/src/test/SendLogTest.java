package test;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import utils.Constants;
import game.AuditLog;

public class SendLogTest {

	AuditLog l1 = new AuditLog();
	AuditLog l2 = new AuditLog();
	
	public static void main(String[] argv) {
		new Thread(new Listener()).start();
		new Thread(new Talker()).start();
	}
	
}

class Listener implements Runnable {

	@Override
	public void run() {
		try {
			ServerSocket ss = new ServerSocket(Constants.CLIENT_HOST_PORT);
			Socket s = ss.accept();
			
			ObjectOutputStream out = new ObjectOutputStream(s.getOutputStream());
			ObjectInputStream in = new ObjectInputStream(s.getInputStream());
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
}

class Talker implements Runnable {

	@Override
	public void run() {
		try {
			Socket s = new Socket(InetAddress.getLocalHost(), Constants.CLIENT_HOST_PORT);
			
			ObjectOutputStream out = new ObjectOutputStream(s.getOutputStream());
			ObjectInputStream in = new ObjectInputStream(s.getInputStream());
			
			ArrayList<String> logs = new ArrayList<String>();
			logs.add("winner:Atlee\n0\tAtlee\t3,4;4,4;\n1\tnewUser\t13,4;12,4;");
			logs.add("winner:Atlee\n0\tAtlee\t3,4;4,4;\n1\tnewUser\t13,4;12,4;");
			out.writeObject(2);
			for (String log : logs) {
				out.writeObject(log);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
}