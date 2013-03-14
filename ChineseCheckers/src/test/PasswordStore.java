package test;

import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.io.Serializable;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.security.spec.X509EncodedKeySpec;
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
	public static void main(String[] args) throws Exception {
		PasswordStore pws = new PasswordStore();
		System.out.println(pws.addEntry("test1", "password1".toCharArray()));
		System.out.println(pws.getEntry("test1").username);
		System.out.println(pws.containsEntry("test1"));
		System.out.println(pws.containsEntry("false"));
		System.out.println(pws.authenticate("test1", "password1".toCharArray()));
		System.out.println(pws.authenticate("test1", "password2".toCharArray()));
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
		DataInputStream in = new DataInputStream(new FileInputStream(f));
		PasswordFileEntry e = null;
		
		int numEntries = in.readInt();
		boolean found = false;
		
		for (int i = 0; i < numEntries; i++) {
			e = readPWEntryFromStream(in);
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
			RandomAccessFile raf = new RandomAccessFile(f, "rw");
			//write the number of entries in the file
			raf.writeInt(0);
			raf.close();
		}
		return f;
	}
	
	/** writes an entry to the file.
	 *  
	 */
	private void writeEntry(PasswordFileEntry entry) throws Exception {
		File f = getPasswordFile();
		RandomAccessFile raf = new RandomAccessFile(f, "rw");
		entry.writeToStream(new FileOutputStream(f, true));
		
		raf.seek(0);
		int numEntries = raf.readInt();
		System.out.println(numEntries);
		numEntries++;
		raf.seek(0);
		raf.writeInt(numEntries);
		raf.close();
	}
	
	private boolean containsEntry(String username) throws Exception {
		PasswordFileEntry entry = getEntry(username);
		if (entry == null) {
			return false;
		}
		return true;
	}
	
	public boolean addEntry(String username, char[] password) throws Exception {
		if (containsEntry(username)) {
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
	
	public PasswordFileEntry readPWEntryFromStream(InputStream inStream) 
			throws Exception {
		DataInputStream in = new DataInputStream(inStream);
		int len = in.readInt();
		char[] usernameChars = new char[len];
		for (int i = 0; i < len; i++) {
			usernameChars[i] = in.readChar();
		}
		len = in.readInt();
		byte[] salt = new byte[len];
		for (int i = 0; i < len; i++) {
			salt[i] = in.readByte();
		}
		len = in.readInt();
		byte[] encryptedPW = new byte[len];
		for (int i = 0; i < len; i++) {
			encryptedPW[i] = in.readByte();
		}
		return new PasswordFileEntry(new String(usernameChars), salt, encryptedPW);
	}
	
	class PasswordFileEntry implements Serializable {
		
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
		
		public void writeToStream(OutputStream outStream) throws Exception {
			DataOutputStream out = new DataOutputStream(outStream);
			out.writeInt(username.length());
			out.writeChars(username);
			out.writeInt(this.salt.length);
			out.write(salt);
			out.writeInt(encryptedPW.length);
			out.write(encryptedPW);
			out.close();
		}
	}

}
