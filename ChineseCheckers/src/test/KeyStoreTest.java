package test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.SecureRandom;

public class KeyStoreTest {

	private static final String KEY_STORE_TYPE = "jks";
	private static final String PUBLIC_KEY_ALG = "DSA";
	private static final String SECURE_RANDOM_ALG = "SHA1PRNG";
	private static final String KEY_PAIR_FILE = "KeyPair.key";
	private static final int PUBLIC_KEY_SIZE = 1024;

	/**
	 * 
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		KeyStore ks = createKeyStore();
		KeyPair pair = getKeyPair();
		
		

	}
	
	private static KeyPair getKeyPair() throws Exception {
		File f = new File(KEY_PAIR_FILE);
		KeyPair pair = null;
		if (!f.exists()) {
			pair = createKeyPair();
		} else {
			pair = loadKeyPair();
		}
		return pair;
	}
	
	private static KeyPair loadKeyPair() throws Exception {
		ObjectInputStream in = new ObjectInputStream(new FileInputStream(KEY_PAIR_FILE));
		KeyPair pair = (KeyPair) in.readObject();
		in.close();
		return pair;
	}
	
	private static KeyPair createKeyPair() throws Exception {
		KeyPairGenerator keyGen = KeyPairGenerator.getInstance(PUBLIC_KEY_ALG);
		SecureRandom random = SecureRandom.getInstance(SECURE_RANDOM_ALG);
		keyGen.initialize(PUBLIC_KEY_SIZE, random);
		
		KeyPair pair = keyGen.generateKeyPair();
		ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(KEY_PAIR_FILE));
		out.writeObject(pair);
		out.close();
		return pair;
	}
	
	private static KeyStore createKeyStore() throws Exception {
		KeyStore ks = KeyStore.getInstance(KEY_STORE_TYPE);
		return ks;
	}
	
	
	private static void saveKeyStore(KeyStore ks, String filename, char[] password) throws Exception {
		FileOutputStream fout = new FileOutputStream(filename);
		ks.store(fout, password);
	}
	
	private static KeyStore loadKeyStore(String filename) {
		
		return null;
	}

}
