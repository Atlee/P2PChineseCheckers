package utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.KeyStoreSpi;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.PublicKey;
import java.security.UnrecoverableEntryException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;

public class MyKeyStore {
	
	private static PublicKey hubPublicKey;

	private KeyStore ks = null;
	
	public MyKeyStore() {
		loadKeyStore(Constants.KEYSTORE_FILE);
	}
	
	/** 
	 * Load the KeyStore from a file. 
	 * If the specified file does not exist, load an empty KeyStore.
	 * 
	 * @param filename 
	 */
	public void loadKeyStore(String filename) {
		if(hubPublicKey == null) initHubPublicKey();
		
		try {
			ks = KeyStore.getInstance(KeyStore.getDefaultType());
			
			FileInputStream fis = null;
			try {
				fis = new java.io.FileInputStream(filename);
				ks.load(fis, null);
			} catch (FileNotFoundException e) {
				ks.load(null, null);
			} finally {
				if (fis != null) {
					fis.close();
				}
			}

		} catch (KeyStoreException | NoSuchAlgorithmException | 
				CertificateException | IOException e) {
			System.out.println("Failed to load the KeyStore!");
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	private static void initHubPublicKey() {
		try {
			ObjectInputStream in = new ObjectInputStream((new FileInputStream("public.key")));
			PublicKey key = (PublicKey) in.readObject();
			in.close();
			hubPublicKey = key;
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	/** 
	 * Save the KeyStore to a file.
	 * 
	 * @param filename 
	 */
	public void saveKeyStore(String filename) throws IOException {
	    java.io.FileOutputStream fos = null;
	    try {
	        fos = new FileOutputStream(filename);
	        ks.store(fos, null);
		} catch (KeyStoreException | NoSuchAlgorithmException | CertificateException e) {
			System.out.println("Failed to save the KeyStore!");
			e.printStackTrace();
			System.exit(1);
		} finally {
	        if (fos != null) {
	            fos.close();
	        }
	    }
	}
	
	/** 
	 * Retrieve the public key from the public key certificate stored under the given alias
	 * in the KeyStore. Return null if no such key exists.
	 * 
	 * @param alias
	 */
	public PublicKey getPublicKey(String alias) {
		PublicKey key = null;
		try {
			Certificate cert = ks.getCertificate(alias);
			key = cert.getPublicKey();
		} catch (KeyStoreException e) {
			System.out.println("Error retrieving public key for "+alias);
			e.printStackTrace();
		}
		return key;
	}
	
	/** 
	 * Retrieve the Hub's public key.
	 */
	public static PublicKey getHubPublicKey() {
		return hubPublicKey;
	}
	
	/** 
	 * Retrieve the private key stored under the given alias in the KeyStore. Use the
	 * given password to access this KeyStore entry. Return null if no such key exists.
	 * 
	 * @param ks
	 * @param alias
	 * @param password
	 */
	public PrivateKey getPrivateKey(String alias, char[] password) {
		PrivateKey key = null;
		try {
			key = (PrivateKey) ks.getKey(alias, password);
		} catch (NoSuchAlgorithmException | UnrecoverableEntryException | KeyStoreException e) {
			System.out.println("Error retrieving private key for "+alias);
			e.printStackTrace();
		}
		
		return key;
	}
	
	/** 
	 * Add the public key certificate to the KeyStore under the given alias.
	 * 
	 * @param ks
	 * @param cert
	 * @param alias
	 */
	public void addPublicKeyCertificate(String alias, Certificate cert) {
		try {
			ks.setCertificateEntry(alias, cert);
		} catch (KeyStoreException e) {
			System.out.println("Error storing public key certificate for "+alias);
			e.printStackTrace();
			System.exit(1);
		}
	}

	/** 
	 * Add the private key (and associated public key certificate) to the KeyStore under
	 * the given alias. Restrict access to this KeyStore entry by the given password.
	 * 
	 * @param ks
	 * @param alias
	 * @param password
	 */
	public void addPrivateKey(PrivateKey key, Certificate cert, String alias, char[] password) {
		Certificate[] chain = {cert};
		try {
			ks.setKeyEntry(alias, key, password, chain);
		} catch (KeyStoreException e) {
			System.out.println("Error storing private key for "+alias);
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	public void addPrivateKey(PrivateKey key, String alias, char[] password) {
		addPrivateKey(key, null, alias, password);
	}
	
	
	
}
