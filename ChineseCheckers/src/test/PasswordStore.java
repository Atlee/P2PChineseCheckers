package test;

import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Arrays;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class PasswordStore {

	private static final String ENCRYPTION_ALG = "PBKDF2WithHmacSHA1";
	private static final int ITERATIONS = 2000;
	//SHA1 creates 160 bit hashes
	private static final int KEY_LEN = 160;
	private static final String SECURE_RANDOM_ALG = "SHA1PRNG";
	private static final int SALT_SIZE = 8;
	private static final String PASSWORD_FILE_NAME = "passwords.txt";
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
	public boolean authenticate(String username, char[] passwordAttempt) 
			throws Exception {
		PasswordFileEntry entry = getEntry(username);
		if (entry == null) {
			return false;
		}
		
		byte[] encryptedAttemptedPW = encrypt(passwordAttempt, entry.salt);
		
		return Arrays.equals(entry.encryptedPW, encryptedAttemptedPW);
	}
	
	/** returns the object representing the entry for the username
	 * if username is not in the file, returns null
	 * 
	 * @param username - the user we are looking for in the file
	 * @return - the file entry for that user
	 */
	private PasswordFileEntry getEntry(String username) throws Exception {
		File f = getPasswordFile();
		ObjectInputStream in = new ObjectInputStream(new FileInputStream(f));
		PasswordFileEntry e = null;
		int numEntries = in.readInt();
		boolean found = false;
		
		for (int i = 0; i < numEntries; i++) {
			e = (PasswordFileEntry) in.readObject();
			if (e.equals(username)) {
				found = true;
				break;
			}
		}
		in.close();
		
		if (found) {
			return e;
		} else {
			return null;
		}
	}
	
	private byte[] encrypt(char[] password, byte[] salt) throws Exception {
		KeySpec spec = new PBEKeySpec(password, salt, ITERATIONS, KEY_LEN);
		
		SecretKeyFactory f = SecretKeyFactory.getInstance(ENCRYPTION_ALG);
		return f.generateSecret(spec).getEncoded();
	}
	
	private File getPasswordFile() throws Exception {
		File f = new File(PASSWORD_FILE_NAME);
		if (!f.exists()) {
			f.createNewFile();
			ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(f));
			//write the number of entries in the file
			out.writeInt(0);
		}
		return f;
	}
	
	/** writes an entry to the file.  in the format 
	 *  
	 */
	private void writeEntry(PasswordFileEntry entry) throws Exception {
		File f = getPasswordFile();
		ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(f, true));
		
		out.writeObject(entry);
		out.close();
	}
	
	private boolean usernameInUse(String username) throws Exception {
		PasswordFileEntry entry = getEntry(username);
		if (entry == null) {
			return false;
		}
		return true;
	}
	
	private boolean addEntry(String username, char[] password) throws Exception {
		if (usernameInUse(username)) {
			return false;
		}
		byte[] salt = generateSalt();
		byte[] encryptedPW = encrypt(password, salt);
		Arrays.fill(password, ' ');
		
		PasswordFileEntry entry = new PasswordFileEntry(username, salt, encryptedPW);
		
		writeEntry(entry);
		return true;
	}
	
	private static byte[] generateSalt() throws Exception {
		SecureRandom random = SecureRandom.getInstance(SECURE_RANDOM_ALG);
		
		byte[] salt = new byte[SALT_SIZE];
		random.nextBytes(salt);
		
		return salt;
	}
	
	class PasswordFileEntry {
		String username;
		byte[] salt;
		byte[] encryptedPW;
		
		public PasswordFileEntry(String username, byte[] salt, byte[] encryptedPW) {
			this.username = username;
			this.salt = salt;
			this.encryptedPW = encryptedPW;
		}
		
		public boolean equals(String username) {
			return this.username.equals(username);
		}
	}

}
