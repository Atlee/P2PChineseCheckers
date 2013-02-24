package client;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.net.Socket;
import java.nio.CharBuffer;
import java.security.KeyStore;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Scanner;
import java.io.ByteArrayOutputStream;
import java.io.Console;
import java.io.DataInputStream;
import java.io.DataOutputStream;
<<<<<<< HEAD
import java.io.FileNotFoundException;
=======
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
>>>>>>> Hub and Peer communication with signed messages
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import utils.EncryptUtils;

public class ClientMain {

	/**
	 * @param args
	 */

	private static final int PORT_NUM = 4321;
	private static final byte[] HUB_PUBLIC_KEY = new byte[] {
		(byte) 0x30, (byte) 0x81, (byte) 0x9f, (byte) 0x30, (byte) 0x0d, (byte) 0x06, (byte) 0x09, (byte) 0x2a, (byte) 0x86, (byte) 0x48, (byte) 0x86, (byte) 0xf7, (byte) 0x0d, (byte) 0x01, (byte) 0x01, (byte) 0x01, (byte) 0x05, (byte) 0x00, (byte) 0x03, (byte) 0x81, (byte) 0x8d, (byte) 0x00, (byte) 0x30, (byte) 0x81, (byte) 0x89, (byte) 0x02, (byte) 0x81, (byte) 0x81, (byte) 0x00, (byte) 0xa0, (byte) 0x02, (byte) 0x9c, (byte) 0xd6, (byte) 0x03, (byte) 0x2d, (byte) 0x6f, (byte) 0xf1, (byte) 0xd6, (byte) 0x83, (byte) 0x8c, (byte) 0xe4, (byte) 0xb6, (byte) 0xec, (byte) 0xee, (byte) 0x9c, (byte) 0x90, (byte) 0x44, (byte) 0x88, (byte) 0x69, (byte) 0xfe, (byte) 0xd0, (byte) 0x0e, (byte) 0xa7, (byte) 0x69, (byte) 0x1f, (byte) 0x98, (byte) 0x82, (byte) 0xf5, (byte) 0x2d, (byte) 0x8c, (byte) 0xae, (byte) 0x25, (byte) 0x7b, (byte) 0x81, (byte) 0xe2, (byte) 0x9f, (byte) 0xea, (byte) 0x9a, (byte) 0xb3, (byte) 0xd8, (byte) 0x46, (byte) 0x31, (byte) 0x62, (byte) 0x0b, (byte) 0xf7, (byte) 0xef, (byte) 0x63, (byte) 0x11, (byte) 0x83, (byte) 0xf3, (byte) 0x02, (byte) 0x53, (byte) 0x1d, (byte) 0x52, (byte) 0x38, (byte) 0xb6, (byte) 0xcb, (byte) 0x79, (byte) 0x9c, (byte) 0x0a, (byte) 0xe4, (byte) 0x09, (byte) 0xfe, (byte) 0xd9, (byte) 0x36, (byte) 0x0a, (byte) 0x6c, (byte) 0xb1, (byte) 0xa1, (byte) 0x1a, (byte) 0xb2, (byte) 0xa4, (byte) 0x74, (byte) 0x5a, (byte) 0x9a, (byte) 0x12, (byte) 0x22, (byte) 0xb4, (byte) 0x0d, (byte) 0x9e, (byte) 0x19, (byte) 0x59, (byte) 0x75, (byte) 0xb4, (byte) 0x79, (byte) 0xb8, (byte) 0xc7, (byte) 0x2d, (byte) 0x40, (byte) 0x09, (byte) 0x69, (byte) 0x11, (byte) 0x46, (byte) 0x0c, (byte) 0x5f, (byte) 0xb5, (byte) 0x1d, (byte) 0x27, (byte) 0x47, (byte) 0x5c, (byte) 0xb9, (byte) 0x9a, (byte) 0x31, (byte) 0xab, (byte) 0x1a, (byte) 0xf7, (byte) 0xd7, (byte) 0xdc, (byte) 0xa4, (byte) 0x01, (byte) 0xa2, (byte) 0xb7, (byte) 0x69, (byte) 0x9a, (byte) 0xef, (byte) 0x46, (byte) 0x9a, (byte) 0xa9, (byte) 0x60, (byte) 0x5d, (byte) 0xd6, (byte) 0x48, (byte) 0x2f, (byte) 0x43, (byte) 0xa2, (byte) 0x15, (byte) 0x65, (byte) 0x02, (byte) 0x03, (byte) 0x01, (byte) 0x00, (byte) 0x01
	};
	private static final String ALGORITHM = "RSA";
	private static final String KEY_STORE_FILE = "TheKeyStore";

	private static KeyStore keyStore;

	public static void main(String[] args) {
		if(keyStore == null) {
			initKeyStore();
		}
		ClientProtocol p = createProtocol();
		listenSocket(p);
	}

	public static void listenSocket(ClientProtocol p) {
		//Create socket connection
		Socket socket = null;
		BufferedReader console;
		DataInputStream in;
		DataOutputStream out;
		InetAddress host = null;
		String userInput, frmServerString;
		byte[] fromServer;

		PublicKey key = getHubPublicKey();

		try{
			host = InetAddress.getLocalHost();

			socket = new Socket(host, PORT_NUM);
<<<<<<< HEAD
			out = new DataOutputStream(socket.getOutputStream());
			in = new DataInputStream(socket.getInputStream());

			console = new BufferedReader(new InputStreamReader(System.in));

			while ((fromServer = readMessage(in)) != null) {
				frmServerString = EncryptUtils.decrypt(fromServer);
				System.out.println("Server: " + frmServerString);

				if (frmServerString.equals("End")) {
					break;
				}

				userInput = p.processInput(frmServerString);
				if (userInput != null) {
					System.out.println("Client: " + userInput);
					byte[] cipherText = EncryptUtils.encryptWithKey(userInput, key);
					sendMessage(out, cipherText);
				}
			}

			out.close();
			in.close();
			console.close();
			socket.close();
		} catch (UnknownHostException e) {
			System.out.println("Unknown host: " + host);
			System.exit(1);
		} catch  (IOException e) {
			System.out.println("No I/O");
			System.exit(1);
		}
	}

	private static byte[] readMessage(DataInputStream in) {
		byte[] buffer = null;
		try {
			int length = in.readInt();
			buffer = new byte[length];
			in.read(buffer, 0, length);
		} catch (IOException e) {
			System.out.println("Error processing input from Socket");
			System.exit(1);
		}
		return buffer;
	}

	private static void sendMessage(DataOutputStream out, byte[] message) {
		try {
			out.writeInt(message.length);
			out.write(message);
		} catch (IOException e) {
			System.out.println("Error writing to socket");
			System.exit(1);
		}
	}

	private static ClientProtocol createProtocol() {
		BufferedReader stdin = new BufferedReader(
				new InputStreamReader(System.in));
		String userInput = null;
		ClientProtocol p;

		System.out.println("Please enter username or \"create\" to create a new account");

		try {
			userInput = stdin.readLine();
		} catch (IOException e) {
			System.out.println("Error reading user input.");
			System.exit(1);
		}

		if (userInput.equals("create")) {
			p = new CreateUserProtocol();
		} else {
			p = new ClientProtocol();
		}

		return p;
	}

	private static PublicKey getHubPublicKey() {
		PublicKey key = null;
		try {
			KeyFactory kf = KeyFactory.getInstance(ALGORITHM);
			X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(HUB_PUBLIC_KEY);

			key = kf.generatePublic(pubKeySpec);
		} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
			// TODO Auto-generated catch block
			System.out.println("publicKey recovery failed");
			System.exit(1);
		}
		return key;
	}
	
	private static PublicKey getPublicKey(String user) {
		PublicKey key = null;
		// TODO: Retrieve the public key under alias "user" from the KeyStore.
		//       If there's no such key in the local KeyStore, return null.
		return key;
	}

	private static void initKeyStore(){
		try {
			keyStore = KeyStore.getInstance(KeyStore.getDefaultType());

			java.io.FileInputStream fis = null;
			try {
				fis = new java.io.FileInputStream(KEY_STORE_FILE);
				keyStore.load(fis, null);
			} catch (FileNotFoundException e) {
				// When fis == null, this loads a new empty KeyStore
				keyStore.load(fis, null);
			} finally {
				if (fis != null) {
					fis.close();
				}
			}
		} catch (Exception e) {
			System.out.println("failed to load the KeyStore");
			System.exit(1);
		}
	}
}
