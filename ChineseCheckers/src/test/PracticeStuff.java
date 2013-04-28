package test;

import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

import utils.Constants;

public class PracticeStuff {

	public static void main(String[] args) {
		KeyGenerator gen = null;
		SecureRandom rand = null;
		try {
			gen = KeyGenerator.getInstance("DES");
			rand = SecureRandom.getInstance(Constants.RANDOM_ALGORITHM);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		gen.init(rand);
		
		Key key = gen.generateKey();
		
		byte[] keyBytes = key.getEncoded();
		for (int i = 0; i < 5; i++) {
			System.out.println(keyBytes[i]);
		}
		
		SecretKeyFactory kf = null;
		KeySpec spec = null;
		Key key2 = null;
		try {
			kf = SecretKeyFactory.getInstance("DES");
			spec = new DESKeySpec(keyBytes);
			key2 = kf.generateSecret(spec);
		} catch (NoSuchAlgorithmException | InvalidKeyException | InvalidKeySpecException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(1);
		}
		
		
		byte[] key2Bytes = key2.getEncoded();
		for (int i = 0; i < 5; i++) {
			System.out.println(key2Bytes[i]);
		}
		
		try {
			Cipher c = Cipher.getInstance("DES");
			c.toString();
			
		} catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
			
		}
	}
}
