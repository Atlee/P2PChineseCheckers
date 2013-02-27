package utils;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import java.net.Socket;

import java.security.PrivateKey;
import java.security.PublicKey;


public abstract class Protocol {
	
	protected void sendSignedMessage(Socket s, byte[] message, PrivateKey key) {
		byte[] signature = SignUtils.signData(key, message);
		sendMessage(s, signature);
		sendMessage(s, message);
	}
	
	protected void sendMessage(Socket s, byte[] message) {
		try {
			DataOutputStream out = new DataOutputStream(s.getOutputStream());
			out.writeInt(message.length);
			out.write(message);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	protected byte[] readSignedMessage(Socket s, PublicKey key) {
		byte[] signature = null;
		byte[] message = null;
		try {
			DataInputStream in = new DataInputStream(s.getInputStream());
			
			int sigLen = in.readInt();
			signature = new byte[sigLen];
			in.read(signature, 0, sigLen);
			
			int messageLen = in.readInt();
			message = new byte[messageLen];
			in.read(message, 0, messageLen);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		boolean success = SignUtils.verifyData(key, signature, message);
		if (success) {
			return message;
		} else {
			return null;
		}
	}
	
	protected byte[] readMessage(Socket s) {
		byte[] output = null;
		try {
			DataInputStream in = new DataInputStream(s.getInputStream());
			int len = in.readInt();
			output = new byte[len];
			in.read(output, 0, len);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		return output;
	}	
}
