package utils;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Constants {
	
	// Hub <-> Client protocol IDs
	public static final int REGISTER = 0;
	public static final int LOGIN = 1;
	public static final int HELLO = 2;
	public static final int GET_GAMES = 3;
	public static final int LOGOUT = 4;
	public static final int JOIN_GAME = 5;
	public static final int GET_GAME_PLAYERS = 6;
	public static final int HOST_GAME = 7;
	public static final int LEAVE = 8;
	public static final int READY = 9;
	
	// Hub application constants
	public static final String HUB_KS_FILENAME = "hub.private";
	public static final String HUB_KS_PASSWORD = "hubpassword";
	public static final int HUB_PORT = 4321;
	
	// Old constants, not sure what they are for - so I won't mess with them
	
	public static final int CLIENT_HOST_PORT = 4444;
    
	public static final String KEYGEN_ALGORITHM = "RSA";
	public static final String SIGN_ALGORITHM = "SHA512withRSA";
	public static final String RANDOM_ALGORITHM = "SHA1PRNG";
	public static final String SHARED_ENCRYPT_ALG = "DES";
	public static final String PUBLIC_ENCRYPT_ALG = "RSA";
	
	public static final String REGISTRATION_SUCCESS = "SUCCESS";
	public static final String REGISTRATION_FAILURE = "FAILURE";
	public static final Object REGISTRATION_PASSWORD = "PASSWORD";
	public static final String REGISTRATION_IN_USE = "IN_USE";
	
	public static final String LOGIN_SUCCESS = "SUCCESS_LOGIN";
	public static final String LOGIN_FAILURE = "FAILURE_LOGIN";
	
	public static final String INVALID_SECRET = "INVALID_SECRET";
	public static final String VALID_SECRET   = "VALID_SECRET";
	
	public static final Object SIGNOUT = "SIGNOUT";
	
	public static final Integer ACK = 0;
	
	public static boolean verifyUsername(String username) {
		Pattern whitespacePattern = Pattern.compile("\\s");
		Matcher whitespaceMatcher = whitespacePattern.matcher(username);
		
		if (username.equals("")) {
			return false;
		}
		if (whitespaceMatcher.find()) {
			return false;
		}
		if (username.length() >= 30) {
			return false;
		}
		return true;
	}
	
	public static boolean verifyPassword(char[] password) {
		boolean output = true;
		
		if (password.length == 0) {
			output = false;
		}
		for (char c : password) {
			if (Character.isWhitespace(c)) {
				output = false;
			}
		}
		Arrays.fill(password, ' ');
		return output;
	}
	
}
