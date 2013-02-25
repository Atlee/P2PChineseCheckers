package peer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;

import utils.KeyStoreUtils;

public class Test {

	/**
	 * @param args
	 * @throws IOException 
	 * @throws UnknownHostException 
	 * @throws NoSuchAlgorithmException 
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		Socket s = new Socket(InetAddress.getLocalHost(), 4321);
		
		KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
		keyGen.initialize(1024);
		KeyPair keys = keyGen.generateKeyPair();
		PublicKey k = keys.getPublic();
		System.out.println("SendingKey");
		sendKey(s, k);
		
		System.out.println(k.hashCode());
		
		BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));
		
		stdin.readLine();

	}
	
	private static void sendKey(Socket s, PublicKey key) throws IOException {
		ObjectOutputStream out = new ObjectOutputStream(s.getOutputStream());
		out.writeObject(key);
	}

}
