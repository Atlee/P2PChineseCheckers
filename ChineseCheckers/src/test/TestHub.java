package test;

import hub.GetHostsProtocol;
import hub.HubConstants;
import hub.HubProtocol;
import hub.JoinHostProtocol;
import hub.NewHostProtocol;
import hub.UserLoginProtocol;
import hub.UserRegistrationProtocol;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.util.Scanner;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

import utils.Constants;
import utils.NetworkUtils;

public class TestHub {
	
	public static void main(String[] argv) {
		ServerSocket server = null;
		try {
			server = new ServerSocket(Constants.HUB_PORT);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		while(true) {
			System.out.println("Please enter response");
			Scanner scn = new Scanner(System.in);
			String response = scn.nextLine();
			try {
				Socket s = server.accept();
				Key sharedKey = handleGetSharedKey(s);
				selectProtocol(s);
				NetworkUtils.sendEncryptedMessage(s, response.getBytes(), sharedKey, Constants.SHARED_ENCRYPT_ALG);
				s.close();
			} catch (IOException e) {
				System.out.println("Server error");
				e.printStackTrace();
				System.exit(1);
			}
		}
	}
	
	private static HubProtocol selectProtocol(Socket s) throws IOException {
		DataInputStream in = null;
		int id = -1;
		HubProtocol p = null;
		
		in = new DataInputStream(s.getInputStream());
		id = in.readInt();

		switch (id) {
		case Constants.REGISTER:
			p = new UserRegistrationProtocol();
			break;
		case Constants.LOGIN:
			p = new UserLoginProtocol();
			break;
		case Constants.GET_HOSTS:
			p = new GetHostsProtocol();
			break;
		case Constants.NEW_HOST:
			p = new NewHostProtocol();
			break;
		case Constants.JOIN_GAME:
			p = new JoinHostProtocol();
			break;
		default:
			System.out.println("Unrecognized protocol ID");
		}
		return p;
	}
	
	private static Key handleGetSharedKey(Socket s) throws IOException {
		try {
			PrivateKey hubPrivate = HubConstants.getHubPrivate();
			byte[] sharedKeyBytes = NetworkUtils.readEncryptedMessage(s, hubPrivate, Constants.PUBLIC_ENCRYPT_ALG);
			
			SecretKeyFactory skf = SecretKeyFactory.getInstance(Constants.SHARED_ENCRYPT_ALG);
			DESKeySpec keySpec = new DESKeySpec(sharedKeyBytes);
			return skf.generateSecret(keySpec);
		} catch (InvalidKeySpecException | NoSuchAlgorithmException | InvalidKeyException e) {
			e.printStackTrace();
			System.exit(1);
		}
		return null;
	}

}
