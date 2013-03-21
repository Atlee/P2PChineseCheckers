package utils;

import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;

public class EncryptUtils {
	
	public static KeyPair newEncryptKeyPair() {
		return SignUtils.newSignKeyPair();
	}
	
	public static KeyPair getEncryptKeyPair(String username, char[] password) {
		KeyPair keys = null;
		try {
			KeyPairGenerator keyGen = KeyPairGenerator.getInstance(Constants.KEYGEN_ALGORITHM);
			
			SecureRandom rand = SecureRandom.getInstance(Constants.RANDOM_ALGORITHM);
			rand.setSeed(("encrypt"+username+(new String(password))).getBytes());
			
			keyGen.initialize(1024, rand);
			keys = keyGen.generateKeyPair();
		} catch (NoSuchAlgorithmException e) {
			System.out.println("Error generating key pair");
			e.printStackTrace();
			System.exit(1);
		}
		return keys;
	}
	
	public static byte[] encryptData(byte[] data, Key key, String encryptAlg) {
		byte[] cipherText = null;
		try {
			Cipher c = Cipher.getInstance(encryptAlg);
			
			c.init(Cipher.ENCRYPT_MODE, key);
			cipherText = c.doFinal(data);
		} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (BadPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return cipherText;
	}
	
	public static byte[] decryptData(byte[] cipherText, Key key, String encryptAlg) {
		byte[] message = null;
		
		try {
			Cipher c = Cipher.getInstance(encryptAlg);
			c.init(Cipher.DECRYPT_MODE, key);
			
			message = c.doFinal(cipherText);
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (BadPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return message;
	}
	
	public static PublicKey getPublicKey(byte[] encodedBytes) {
		X509EncodedKeySpec spec = new X509EncodedKeySpec(encodedBytes);
		try {
			KeyFactory kf = KeyFactory.getInstance(Constants.KEYGEN_ALGORITHM);
			return kf.generatePublic(spec);
		} catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
			return null;
		}
	}
	
	public static PrivateKey getPrivateKey(byte[] encodedBytes) {
		PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(encodedBytes);
		try {
			KeyFactory kf = KeyFactory.getInstance(Constants.KEYGEN_ALGORITHM);
			return kf.generatePrivate(spec);
		} catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
			return null;
		}
	}
	
	public static Key handleCreateSharedKey() {
		Key sharedKey = null;
		try {
			//generate the shared key
			KeyGenerator gen = KeyGenerator.getInstance(Constants.SHARED_KEY_ALGORITHM);
			SecureRandom rand = SecureRandom.getInstance(Constants.RANDOM_ALGORITHM);
			gen.init(rand);
			sharedKey = gen.generateKey();
		} catch (NoSuchAlgorithmException e) {
			
		}
		return sharedKey;
	}
}
