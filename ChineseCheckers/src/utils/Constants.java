package utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.security.PublicKey;
import java.util.Arrays;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Constants {
	
	// Hub <-> Client protocol IDs
	public static final int REGISTER = 0;
	public static final int LOGIN = 1;
	public static final int HELLO = 2;
	
	// Hub application constants
	public static final String HUB_KS_FILENAME = "hub.private";
	public static final String HUB_KS_PASSWORD = "hubpassword";
	public static final int HUB_PORT = 4321;
	public static final int CLIENT_HOST_PORT = 4322;

	public static final String KEYGEN_ALGORITHM = "RSA";
	public static final String SIGN_ALGORITHM = "SHA512withRSA";
	public static final String RANDOM_ALGORITHM = "SHA1PRNG";
	public static final String SHARED_ENCRYPT_ALG = "DES";
	public static final String PUBLIC_ENCRYPT_ALG = "RSA";
	
	public static final String REGISTRATION_SUCCESS = "SUCCESS";
	public static final String REGISTRATION_FAILURE = "FAILURE";
	public static final String REGISTRATION_IN_USE = "IN_USE";
	public static final String REGISTRATION_PASSWORD = "PASSWORD";
	
	public static final String LOGIN_SUCCESS = "SUCCESS_LOGIN";
	public static final String LOGIN_FAILURE = "FAILURE_LOGIN";
	
  //-------------old hub constants--------------------
	public static final String HUB_KEY_FILE = "public.key";
	
	// Protocol IDs
	//public static final int REGISTER = 0;
	//public static final int LOGIN = 1;
	public static final int GET_HOSTS = 2;
	public static final int NEW_HOST = 3;
	public static final int JOIN_GAME = 4;
	public static final int LOGOUT = 5;
	public static final int GET_LOG = 6;
	private static final int MAX_PW_LEN = 100;
	private static final int MIN_PW_LEN = 8;
	private static final int MAX_USERNAME_LEN = 45;
	
	public static final String VERIFY_SUCCESS = "VERIFY_SUCCESS";
	public static final String VERIFY_FAILURE = "VERIFY_FAILURE";
	
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
	//----------------------end old hub constants-----------------------------
  
	public static boolean verifyUsername(String username) {
		Pattern whitespacePattern = Pattern.compile("\\s");
		Matcher whitespaceMatcher = whitespacePattern.matcher(username);
		
		if (username.equals("")) {
			return false;
		}
		if (whitespaceMatcher.find()) {
			return false;
		}
		System.out.println(username.length());
		if (username.length() >= MAX_USERNAME_LEN) {
			return false;
		}
		return true;
	}
	
	public static boolean verifyPassword(char[] password) {
		boolean output = true;
		boolean hasCapital = false;
		boolean hasSymbol = false;
		
		if (password.length <= MIN_PW_LEN || password.length > MAX_PW_LEN) {
			output = false;
		}
		for (char c : password) {
			if (Character.isWhitespace(c)) {
				output = false;
			} else if (Character.isUpperCase(c)) {
				//make sure the password has a capital letter
				hasCapital = true;
			} else if (!Character.isAlphabetic(c)) {
				//make sure the password has a non-alpha character
				hasSymbol = true;
			}			
		}
		Arrays.fill(password, ' ');
		return output && hasCapital && hasSymbol;
	}
	
}
