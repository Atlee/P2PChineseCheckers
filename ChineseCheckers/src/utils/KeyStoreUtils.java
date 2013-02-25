package utils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.KeyStore.PasswordProtection;
import java.security.PublicKey;
import java.security.PrivateKey;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableEntryException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

public class KeyStoreUtils {
	
	private static PublicKey hubPublicKey;
	private static PrivateKey hubPrivateKey; // Don't forget to get rid of this later!

	/** 
	 * Load the KeyStore from a file. 
	 * If the specified file does not exist, load an empty KeyStore.
	 * 
	 * @param filename 
	 */
	public static KeyStore loadKeyStore(String filename) throws IOException {
		//if(hubPublicKey == null) initHubPublicKey();
		if(hubPrivateKey == null) initHubPublicPrivateKeys(); // and to delete this...
		
		KeyStore ks = null;
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

		} catch (KeyStoreException | NoSuchAlgorithmException | CertificateException e) {
			System.out.println("Failed to load the KeyStore!");
			e.printStackTrace();
			System.exit(1);
		}
		return ks;
	}
	
	/** 
	 * Save the KeyStore to a file.
	 * 
	 * @param ks
	 * @param filename 
	 */
	public static void saveKeyStore(KeyStore ks, String filename) throws IOException {
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
	 * @param ks
	 * @param alias
	 */
	public static PublicKey getPublicKey(KeyStore ks, String alias) {
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
	 * Retrieve the Hub's public key.
	 */
	public static PrivateKey getHubPrivateKey() {
		return hubPrivateKey;
	}
	
	/** 
	 * Retrieve the private key stored under the given alias in the KeyStore. Use the
	 * given password to access this KeyStore entry. Return null if no such key exists.
	 * 
	 * @param ks
	 * @param alias
	 * @param password
	 */
	public static PrivateKey getPrivateKey(KeyStore ks, String alias, String password) {
		PrivateKey key = null;
		char[] passwordChars = null;
		password.getChars(0, password.length()-1, passwordChars, 0);
		try {
			key = (PrivateKey) ks.getKey(alias, passwordChars);
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
	public static void addPublicKeyCertificate(KeyStore ks, Certificate cert, String alias) {
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
	public static void addPrivateKey(KeyStore ks, PrivateKey key, Certificate cert, String alias, String password) {
		char[] passwordChars = null;
		password.getChars(0, password.length()-1, passwordChars, 0);
		Certificate[] chain = {cert};
		try {
			ks.setKeyEntry(alias, key, passwordChars, chain);
		} catch (KeyStoreException e) {
			System.out.println("Error storing private key for "+alias);
			e.printStackTrace();
			System.exit(1);
		}
	}

	private static void initHubPublicKey() {
		try {
			KeyFactory kf = KeyFactory.getInstance(Constants.KEYGEN_ALGORITHM);
			X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(Constants.HUB_PUBLIC_KEY);
			
			hubPublicKey = kf.generatePublic(pubKeySpec);
		} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
			System.out.println("Error loading the Hub's public key");
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	private static void initHubPublicPrivateKeys() {
		KeyPair keys = SignUtils.newSignKeyPair();
		hubPublicKey = keys.getPublic();
		hubPrivateKey = keys.getPrivate();
	}
	

}
