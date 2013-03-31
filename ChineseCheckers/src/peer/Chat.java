package peer;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Scanner;

import utils.NetworkUtils;

public class Chat {
	
	Thread sender;
	Thread listener;
	boolean isDone = false;
	Socket s;
	
	public Chat(Socket s) {
		this.s = s;
		this.sender = new Thread(new Sender(s));
		this.listener = new Thread(new Listener(s));
	}
	
	public void start() {
		this.sender.start();
		this.listener.start();
	}
	
	class Sender implements Runnable {
		Socket s;
		
		public Sender(Socket s) {
			this.s = s;
		}
		
		@Override
		public void run() {
			System.out.println("Welcome to AtleeChat");
			System.out.println("Please enter message to send it to your firend or" +
					"enter \"ZZZ\" to quit");
			Scanner scn = new Scanner(System.in);
			String next = null;
			//while the user input doesn't equal ZZZ
			try {
				while (!(next = scn.nextLine()).equals("ZZZ")) {
					NetworkUtils.sendMessage(s, next.getBytes());
				}
				NetworkUtils.sendMessage(s, "ZZZ".getBytes());
				scn.close();
				s.close();
			} catch (SocketException e) {
				;
			} catch (IOException e) {
				e.printStackTrace();
			}
			System.out.println("socket closed");
		}
	}
	
	class Listener extends Thread {
		Socket s;
		
		public Listener(Socket s) {
			this.s = s;
		}
		
		@Override
		public void run() {
			String message = "";
			while (!message.equals("ZZZ")) {
				try {
					message = new String(NetworkUtils.readMessage(s));
					System.out.println(message);
				} catch (SocketException e) {
					break;
				} catch (IOException e) {
					System.out.println("Error reading message from socket");
				}
			}
			try {
				s.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public static void main(String[] argv) throws Exception {
		ServerSocket server = new ServerSocket(4444);
		
		Socket s = server.accept();
		Chat c = new Chat(s);
		c.start();
	}

}
