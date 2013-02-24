package utils;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class SignUtils {
	
	private static final String KEYGEN_ALGORITHM = Constants.KEYGEN_ALGORITHM;
	private static final String SIGN_ALGORITHM = Constants.SIGN_ALGORITHM;
	
	/** 
	 * Generate a new RSA key pair to be used in the digital signature scheme.
	 */
	public static KeyPair newSignKeyPair() {
		KeyPair keys = null;
		try {
			KeyPairGenerator keyGen = KeyPairGenerator.getInstance(KEYGEN_ALGORITHM);
			keyGen.initialize(1024);
			keys = keyGen.generateKeyPair();
		} catch (NoSuchAlgorithmException e) {
			System.out.println("Error generating key pair");
			e.printStackTrace();
			System.exit(1);
		}
		return keys;
	}
	
	/** 
	 * Produce a signature for the input data using the given private key.
	 * 
	 * @param key
	 * @param data
	 */
	public static byte[] signData(PrivateKey key, byte[] data) {
		byte[] sign = null;
		try {
			Signature rsa = Signature.getInstance(SIGN_ALGORITHM);
			rsa.initSign(key);
			rsa.update(data);
			sign = rsa.sign();
		} catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException e) {
			System.out.println("Error signing data");
			e.printStackTrace();
			System.exit(1);
		}
		return sign;
	}
	
	/** 
	 * Verify that the signature on this data was produced using the private key that
	 * corresponds to the given public key.
	 * 
	 * @param key
	 * @param sign
	 * @param data
	 */
	public static boolean verifyData(PublicKey key, byte[] sign, byte[] data) {
		try {
			Signature rsa = Signature.getInstance(SIGN_ALGORITHM);
			rsa.initVerify(key);
			rsa.update(data);
			return rsa.verify(sign);
		} catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException e) {
			System.out.println("Error verifying signature");
			e.printStackTrace();
			System.exit(1);
		}
		return false;
	}

	
}
