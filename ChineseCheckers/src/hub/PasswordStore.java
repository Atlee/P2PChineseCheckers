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
			// TODO Auto-generated catch block
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
			//write the number of entries in the file
			raf.writeInt(0);
			raf.close();
		}
		return f;
	}
	
	/** writes an entry to the file.
	 * @throws IOException 
	 *  
	 */
	private void writeEntry(PasswordFileEntry entry) throws IOException {
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
	
	public boolean containsEntry(String username) throws IOException {
		PasswordFileEntry entry = getEntry(username);
		if (entry == null) {
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
			
			PasswordFileEntry entry = new PasswordFileEntry(username, salt, encryptedPW);
			
			writeEntry(entry);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
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
	
	private PasswordFileEntry readPWEntryFromStream(InputStream inStream) 
			throws IOException {
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
		
		public void writeToStream(OutputStream outStream) throws IOException {
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
