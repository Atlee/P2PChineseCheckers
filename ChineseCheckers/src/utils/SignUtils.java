package utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;

public class SignUtils {
	
	private static final String PUBLIC_KEY_FILE = "public.key";
	private static final String PRIVATE_KEY_FILE = "private.key";
	private static final String ALGORITHM = "RSA";
	private static final String SIGN_ALGORITHM = "SHA512withRSA";
	
	private static boolean isInit = false;
	private static PublicKey publicKey = null;
	private static PrivateKey privateKey = null;

	private static void generateKeys() {
		generateKeys(PUBLIC_KEY_FILE, PRIVATE_KEY_FILE);
	}
	
	private static void generateKeys(String publicFileName, String privateFileName) {
		System.out.println("Generating keys");
		try {
			final KeyPairGenerator keyGen = KeyPairGenerator.getInstance(ALGORITHM);
			keyGen.initialize(1024);
			final KeyPair key = keyGen.generateKeyPair();
				
			File privateKeyFile = new File(privateFileName);
			File publicKeyFile = new File(publicFileName);
				
			// Create files to store public and private key
			if (privateKeyFile.getParentFile() != null) {
				privateKeyFile.getParentFile().mkdirs();
			}
			privateKeyFile.createNewFile();
		
			if (publicKeyFile.getParentFile() != null) {
			    publicKeyFile.getParentFile().mkdirs();
			}
			publicKeyFile.createNewFile();
		
			// Saving the Public key in a file
			ObjectOutputStream publicKeyOOS = new ObjectOutputStream(
					new FileOutputStream(publicKeyFile));
		 	publicKeyOOS.writeObject(key.getPublic());
		 	publicKeyOOS.close();
			  
		 	FileOutputStream fos = new FileOutputStream("public");
		 	fos.write(key.getPublic().getEncoded());
		 	fos.close();
			
		 	// Saving the Private key in a file
		 	ObjectOutputStream privateKeyOOS = new ObjectOutputStream(
		 			new FileOutputStream(privateKeyFile));
		 	privateKeyOOS.writeObject(key.getPrivate());
		 	privateKeyOOS.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static boolean keysExist() {
		return keysExist(PUBLIC_KEY_FILE, PRIVATE_KEY_FILE);
	}
	
	private static boolean keysExist(String publicFileName, String privateFileName) {
		File publicFile = new File(publicFileName);
		File privateFile = new File(privateFileName);
		boolean output = false;
		if (publicFile.exists() && privateFile.exists()) {
			output = true;
		}
		return output;
	}
	
	private static void init() {
		if (!keysExist()) {
			generateKeys();
		}
		try {
			ObjectInputStream pubOIS = new ObjectInputStream(new FileInputStream(PUBLIC_KEY_FILE));
			ObjectInputStream privOIS = new ObjectInputStream(new FileInputStream(PRIVATE_KEY_FILE));
			publicKey = (PublicKey) pubOIS.readObject();
			privateKey = (PrivateKey) privOIS.readObject();
			pubOIS.close();
			privOIS.close();
			isInit = true;
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	public static PublicKey getPublic() {
		if (!isInit) {
			init();
		}
		return publicKey;
	}
	
	public static PrivateKey getPrivate() {
		if (!isInit) {
			init();
		}
		return privateKey;
	}
	
	public static byte[] signData(byte[] data) {
		PrivateKey key = getPrivate();
		return signData(key, data);
	}
	
	public static byte[] signData(PrivateKey key, byte[] data) {
		byte[] output = null;
		try {
			Signature dsa = Signature.getInstance(SIGN_ALGORITHM);
			dsa.initSign(key);
			
			dsa.update(data);
			output = dsa.sign();
		} catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(1);
		}
		return output;
	}
	
	public static boolean verifyData(PublicKey key, byte[] sigToVerify, byte[] data) {
		try {
			Signature sig = Signature.getInstance("SHA512withRSA");
			sig.initVerify(key);
			
			sig.update(data);
			
			return sig.verify(sigToVerify);
		} catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(1);
		}
		return false;
	}
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
