package server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPairGenerator;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class KeyPairScript {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		File pubKeyFile = new File("public");
		File privKeyFile = new File("private");
		if (!pubKeyFile.exists() || !privKeyFile.exists()) {
			BufferedReader stdin = new BufferedReader(
					new InputStreamReader(System.in));
			String alg = "RSA";
			KeyPairGenerator gen = null;
			KeyPair key;
			
			System.out.println("Please specify encryption algorithm to use, or" +
					" press enter for RSA.");
			
			try {
				alg = stdin.readLine();
				if (alg.equals("")) {
					alg = "RSA";
				}
				
				gen = KeyPairGenerator.getInstance(alg);
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.out.println("Error reading input, RSA will be used" +
						" for encryption algorithm");
			} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				System.out.println("Algorithm not recognized.");
				System.exit(1);
			}
			
			key = gen.genKeyPair();
			
			byte[] publicBytes = key.getPublic().getEncoded();
			byte[] privateBytes = key.getPrivate().getEncoded();
			
			try {
				FileOutputStream publicFOS = new FileOutputStream("public");
				FileOutputStream privateFOS = new FileOutputStream("private");
				
				publicFOS.write(publicBytes);
				privateFOS.write(privateBytes);
				
				publicFOS.close();
				privateFOS.close();
			} catch (IOException e) {
				System.out.println("Error writing key to file");
			}
		}
		test();
	}
	
	private static void test() {
		PrivateKey privKey = null;
		PublicKey pubKey = null;
		String message = "Text to be encrypted ";
		byte[] cipherText = null;
		byte[] messageBytes = null;
		try {
			FileInputStream fs = new FileInputStream("private");
			byte[] privKeyBytes = new byte[fs.available()];
			fs.read(privKeyBytes);
			fs.close();
			
			PKCS8EncodedKeySpec privKeySpec = new PKCS8EncodedKeySpec(privKeyBytes);
			
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			privKey = keyFactory.generatePrivate(privKeySpec);
			
		} catch (IOException e) {
			System.out.println("Error opening private key file");
			System.exit(1);
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			System.out.println("Invalid algorithm for keyFactory");
		} catch (InvalidKeySpecException e) {
			// TODO Auto-generated catch block
			System.out.println("invalid private key");
		}
		
		try {
			byte[] pubKeyBytes = new byte[] {(byte) 0x30, (byte) 0x81, (byte) 0x9f, (byte) 0x30, (byte) 0x0d, (byte) 0x06, (byte) 0x09, (byte) 0x2a, (byte) 0x86, (byte) 0x48, (byte) 0x86, (byte) 0xf7, (byte) 0x0d, (byte) 0x01, (byte) 0x01, (byte) 0x01, (byte) 0x05, (byte) 0x00, (byte) 0x03, (byte) 0x81, (byte) 0x8d, (byte) 0x00, (byte) 0x30, (byte) 0x81, (byte) 0x89, (byte) 0x02, (byte) 0x81, (byte) 0x81, (byte) 0x00, (byte) 0x81, (byte) 0x22, (byte) 0xd9, (byte) 0x77, (byte) 0x13, (byte) 0xbe, (byte) 0x1b, (byte) 0x07, (byte) 0x5a, (byte) 0x52, (byte) 0x6f, (byte) 0xfa, (byte) 0xda, (byte) 0x03, (byte) 0x8c, (byte) 0x65, (byte) 0x64, (byte) 0x0b, (byte) 0xb0, (byte) 0x25, (byte) 0x74, (byte) 0xd0, (byte) 0x0f, (byte) 0x78, (byte) 0xdb, (byte) 0x6a, (byte) 0xec, (byte) 0x24, (byte) 0x10, (byte) 0x00, (byte) 0xcc, (byte) 0xb5, (byte) 0x73, (byte) 0x9c, (byte) 0x11, (byte) 0xc2, (byte) 0xc5, (byte) 0x3d, (byte) 0x18, (byte) 0xfb, (byte) 0xb6, (byte) 0x23, (byte) 0xc3, (byte) 0xe8, (byte) 0xb3, (byte) 0xea, (byte) 0x5b, (byte) 0x9e, (byte) 0x86, (byte) 0xce, (byte) 0x15, (byte) 0x1c, (byte) 0xbe, (byte) 0xa8, (byte) 0x08, (byte) 0x7c, (byte) 0xbc, (byte) 0x3b, (byte) 0x66, (byte) 0x77, (byte) 0xc7, (byte) 0xda, (byte) 0x75, (byte) 0x1a, (byte) 0x1d, (byte) 0xed, (byte) 0x40, (byte) 0xfe, (byte) 0xb5, (byte) 0x8b, (byte) 0xb0, (byte) 0x5f, (byte) 0x90, (byte) 0xf7, (byte) 0x1b, (byte) 0x6a, (byte) 0xdf, (byte) 0x57, (byte) 0x24, (byte) 0xda, (byte) 0xa1, (byte) 0x16, (byte) 0x93, (byte) 0xf8, (byte) 0xfe, (byte) 0xe2, (byte) 0x69, (byte) 0x1d, (byte) 0x4b, (byte) 0xeb, (byte) 0x28, (byte) 0xfa, (byte) 0x56, (byte) 0x78, (byte) 0x2b, (byte) 0xe9, (byte) 0x13, (byte) 0xb3, (byte) 0x75, (byte) 0x1e, (byte) 0x25, (byte) 0x54, (byte) 0x4d, (byte) 0xbc, (byte) 0xdd, (byte) 0xa0, (byte) 0x48, (byte) 0x89, (byte) 0xf8, (byte) 0x6a, (byte) 0x36, (byte) 0x64, (byte) 0x8a, (byte) 0x64, (byte) 0x1e, (byte) 0x93, (byte) 0xfd, (byte) 0xb2, (byte) 0x81, (byte) 0x8d, (byte) 0x6b, (byte) 0x9b, (byte) 0xdf, (byte) 0x9a, (byte) 0x19, (byte) 0xd6, (byte) 0x0d, (byte) 0xc7, (byte) 0x02, (byte) 0x03, (byte) 0x01, (byte) 0x00, (byte) 0x01};
			
			X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(pubKeyBytes);
			
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			pubKey = keyFactory.generatePublic(pubKeySpec);
			
		} catch (NoSuchAlgorithmException e) {
			System.out.println("Invalid algorithm for keyFactory");
		} catch (InvalidKeySpecException e) {
			System.out.println("invalid public key");
		}
		
		try {
			Cipher cipher = Cipher.getInstance("RSA");
			cipher.init(Cipher.ENCRYPT_MODE, pubKey);
			
			cipherText = cipher.doFinal(message.getBytes());
			
		} catch (NoSuchAlgorithmException e) {
			System.out.println("Error with encrypt");
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			System.out.println("Error with encrypt");
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			// TODO Auto-generated catch block
			System.out.println("Error with encrypt");
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			// TODO Auto-generated catch block
			System.out.println("Error with encrypt");
			e.printStackTrace();
		} catch (BadPaddingException e) {
			// TODO Auto-generated catch block
			System.out.println("Error with encrypt");
			e.printStackTrace();
		}
		
		try {
			Cipher decrypt = Cipher.getInstance("RSA");
			decrypt.init(Cipher.DECRYPT_MODE, privKey);
			
			messageBytes = decrypt.doFinal(cipherText);
		} catch (NoSuchAlgorithmException e) {
			System.out.println("Error with decrypt");
		} catch (NoSuchPaddingException e) {
			// TODO Auto-generated catch block
			System.out.println("Error with decrypt");
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			System.out.println("Error with decrypt");
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			// TODO Auto-generated catch block
			System.out.println("Error with decrypt");
			e.printStackTrace();
		} catch (BadPaddingException e) {
			// TODO Auto-generated catch block
			System.out.println("Error with decrypt");
			e.printStackTrace();
		}
		
		String decryptMessage = new String(messageBytes);
		
		System.out.println(message + " " + decryptMessage);
	}	
}
