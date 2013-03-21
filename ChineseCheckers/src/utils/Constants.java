package utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.InetAddress;
import java.security.PublicKey;

public class Constants {
	
	public static final int PORT_NUM = 4321;
	public static final String KEYGEN_ALGORITHM = "RSA";
	public static final String SIGN_ALGORITHM = "SHA512withRSA";
	public static final String KEYSTORE_FILE = "TheKeyStore";
	public static final String RANDOM_ALGORITHM = "SHA1PRNG";
	public static final String SHARED_KEY_ALGORITHM = "DES";
	public static final String PUBLIC_ENCRYPT_ALG = "RSA";
	
	public static final String REGISTRATION_SUCCESS = "SUCCESS";
	public static final String REGISTRATION_FAILURE = "FAILURE";
	public static final String REGISTRATION_IN_USE = "IN_USE";
	
	public static final String LOGIN_SUCCESS = "SUCCESS_LOGIN";
	public static final String LOGIN_FAILURE = "FAILURE_LOGIN";
	
	public static final String HUB_KEY_FILE = "public.key";
	
	// Protocol IDs
	public static final int REGISTER = 0;
	public static final int LOGIN = 1;
	public static final int P2P = 2;
	
	private static PublicKey hubKey = null;
	
	public static PublicKey getHubPublicKey() {
		if (hubKey == null) {
			hubKeyInit();
		}
		return hubKey;
	}
	
	private static void hubKeyInit() {
		try {
			ObjectInputStream in = new ObjectInputStream(new FileInputStream(HUB_KEY_FILE));
			hubKey = (PublicKey) in.readObject();
			in.close();
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
			System.exit(1);
		}		
	}
}
