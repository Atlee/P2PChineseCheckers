package hub;

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
import java.io.RandomAccessFile;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Arrays;

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
		System.out.println(pws.addEntry("test1", "password".toCharArray()));
	}
	
	public boolean authenticate(String username, char[] passwordAttempt) {
		PasswordFileEntry entry = null;
		try {
			entry = getEntry(username);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
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
	 * @throws IOException 
	 */
	private PasswordFileEntry getEntry(String username) throws IOException {
		long userOffset = getUserIndex(username);
		if (userOffset != -1) {
			File f = getPasswordFile();
			RandomAccessFile raf = new RandomAccessFile(f, "rw");
			PasswordFileEntry entry = PasswordFileEntry.readEntry(raf, userOffset);
			return entry;
		}
		return null;
	}
	
	private byte[] encrypt(char[] password, byte[] salt) {
		KeySpec spec = new PBEKeySpec(password, salt, ITERATIONS, KEY_LEN);
		
		SecretKeyFactory f;
		byte[] cipherText = null;
		try {
			f = SecretKeyFactory.getInstance(ENCRYPTION_ALG);
			cipherText = f.generateSecret(spec).getEncoded();
		} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return cipherText;
	}
	
	private File getPasswordFile() throws IOException {
		File f = new File(PASSWORD_FILE_NAME);
		if (!f.exists()) {
			f.createNewFile();
			RandomAccessFile raf = new RandomAccessFile(f, "rw");
			raf.close();
		}
		return f;
	}
	
	private long getUserIndex(String user) throws IOException {
		File f = getPasswordFile();
		RandomAccessFile raf = new RandomAccessFile(f, "r");
		
		while(raf.getFilePointer() < raf.length()) {
			long entryStart = raf.getFilePointer();
			
			PasswordFileEntry entry = PasswordFileEntry.readEntry(raf, entryStart);
			if (entry.equals(user)) {
				raf.close();
				return entryStart;
			}
			
			long nextPointer = entryStart + StatsFileEntry.MAX_BLOB_SIZE;
			raf.seek(nextPointer);
		}
		raf.close();		
		return -1;
	}
	
	public boolean containsEntry(String username) throws IOException {
		long index = getUserIndex(username);
		if (index == -1) {
			return false;
		}
		return true;
	}
	
	public boolean addEntry(String username, char[] password) {
		try {
			if (containsEntry(username)) {
				return false;
			}

			byte[] salt = generateSalt();
			byte[] encryptedPW = encrypt(password, salt);
			Arrays.fill(password, ' ');
			long index = getUserIndex(" ");
			File f = getPasswordFile();
			RandomAccessFile raf = new RandomAccessFile(f, "rw");
			if (index == -1) {
				index = raf.length() + PasswordFileEntry.MAX_ENTRY_SIZE - (raf.length() % PasswordFileEntry.MAX_ENTRY_SIZE);
			}
			
			PasswordFileEntry entry = new PasswordFileEntry(username, salt, encryptedPW);
			
			entry.writeEntry(raf, index);
			raf.close();
			return true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
	public void replaceEntry(String user, char[] password) throws IOException {
		long userOffset = getUserIndex(user);
		if (userOffset == -1) {
			addEntry(user, password);
		} else {
			File f = getPasswordFile();
			RandomAccessFile raf = new RandomAccessFile(f, "rw");
			raf.seek(userOffset);
			
			byte[] salt = generateSalt();
			byte[] encryptedPW = encrypt(password, salt);
			Arrays.fill(password, ' ');
			
			new PasswordFileEntry(user, salt, encryptedPW).writeEntry(raf, userOffset);
			raf.close();
		}
	}
	
	public boolean removeEntry(String username) {
		try {
			long index = getUserIndex(username);
			
			if (index == -1) {
				return true;
			}
			File f = getPasswordFile();
			RandomAccessFile raf = new RandomAccessFile(f, "rw");
			
			PasswordFileEntry emptyEntry = new PasswordFileEntry(" ", new byte[0], new byte[0]);
			emptyEntry.writeEntry(raf, index);
			raf.close();
			return true;
		} catch(IOException ex) {
			ex.printStackTrace();
		}
		return false;
	}
	
	private static byte[] generateSalt() {
		SecureRandom random = null;
		try {
			random = SecureRandom.getInstance(SECURE_RANDOM_ALG);
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		byte[] salt = new byte[SALT_SIZE];
		random.nextBytes(salt);
		
		return salt;
	}
}

class PasswordFileEntry {
	
	static int MAX_ENTRY_SIZE = 1024;
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
	
	public boolean writeEntry(RandomAccessFile raf, long index) throws IOException {
		if (Integer.SIZE * 3 + username.length() + salt.length + encryptedPW.length > MAX_ENTRY_SIZE) {
			return false;
		}
		raf.seek(index);
		raf.writeInt(username.length());
		raf.writeChars(username);
		raf.writeInt(this.salt.length);
		raf.write(salt);
		raf.writeInt(encryptedPW.length);
		raf.write(encryptedPW);
		return true;
	}
	
	static PasswordFileEntry readEntry (RandomAccessFile raf, long entryStart) 
			throws IOException {
		raf.seek(entryStart);
		int len = raf.readInt();
		char[] usernameChars = new char[len];
		for (int i = 0; i < len; i++) {
			usernameChars[i] = raf.readChar();
		}
		len = raf.readInt();
		byte[] salt = new byte[len];
		for (int i = 0; i < len; i++) {
			salt[i] = raf.readByte();
		}
		len = raf.readInt();
		byte[] encryptedPW = new byte[len];
		for (int i = 0; i < len; i++) {
			encryptedPW[i] = raf.readByte();
		}
		return new PasswordFileEntry(new String(usernameChars), salt, encryptedPW);
	}
}
