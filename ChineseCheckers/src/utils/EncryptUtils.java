package utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;

import javax.crypto.Cipher;

/**
 * @author JavaDigest
 * 
 */
public class EncryptUtils {

  /**
   * String to hold name of the encryption algorithm.
   */
  public static final String ALGORITHM = "RSA";

  /**
   * String to hold the name of the private key file.
   */
  public static final String PRIVATE_KEY_FILE = "private.key";

  /**
   * String to hold name of the public key file.
   */
  public static final String PUBLIC_KEY_FILE = "public.key";

  /**
   * Generate key which contains a pair of private and public key using 1024
   * bytes. Store the set of keys in Prvate.key and Public.key files.
   * 
   * @throws NoSuchAlgorithmException
   * @throws IOException
   * @throws FileNotFoundException
   */
	public static void generateKey(String publicFileName, String privateFileName) {
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
			ObjectOutputStream publicKeyOS = new ObjectOutputStream(
					new FileOutputStream(publicKeyFile));
		 	publicKeyOS.writeObject(key.getPublic());
		 	publicKeyOS.close();
			  
		 	FileOutputStream fos = new FileOutputStream("public");
		 	fos.write(key.getPublic().getEncoded());
		 	fos.close();
			
		 	// Saving the Private key in a file
		 	ObjectOutputStream privateKeyOS = new ObjectOutputStream(
		 			new FileOutputStream(privateKeyFile));
		 	privateKeyOS.writeObject(key.getPrivate());
		 	privateKeyOS.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	
	}
	
	public static PublicKey getPublicKey() {
		return getPublicKey("public.key");
	}
  
  public static PublicKey getPublicKey(String filename) {
	  PublicKey key = null;
	  try {
		  ObjectInputStream fin = new ObjectInputStream(
				  new FileInputStream(filename));
		  key = (PublicKey) fin.readObject();
		  fin.close();
	  } catch (IOException | ClassNotFoundException e) {
		  System.out.println("Error reading key from file");
		  System.exit(1);
	  }
	  return key;
  }
  
  public static PrivateKey getPrivateKey() {
	  return getPrivateKey("private.key");
  }
  
  public static PrivateKey getPrivateKey(String filename) {
	  PrivateKey key = null;
	  try {
		  ObjectInputStream fin = new ObjectInputStream(
				  new FileInputStream(filename));
		  key = (PrivateKey) fin.readObject();
		  fin.close();
	  } catch (IOException | ClassNotFoundException e) {
		  System.out.println("Error reading key from file");
		  e.printStackTrace();
		  System.exit(1);
	  }
	  return key;
  }
  
  private static boolean isKeyPresent(String filename) {
	  File f = new File(filename);
	  boolean output = false;
	  if (f.exists()) {
		  output = true;
	  }
	  return output;
  }


  /**
   * The method checks if the pair of public and private key has been generated.
   * 
   * @return flag indicating if the pair of keys were generated.
   */
  public static boolean areKeysPresent(String publicFileName, String privateFileName) {

    return isKeyPresent(publicFileName) && isKeyPresent(privateFileName);
  }
  
  	public static byte[] encrypt(String text) {
		byte[] cipherText = null;
		PublicKey key = null;
		try {
			ObjectInputStream inputStream = null;
	
		    // Encrypt the string using the public key
		    inputStream = new ObjectInputStream(new FileInputStream(PUBLIC_KEY_FILE));
		    key = (PublicKey) inputStream.readObject();
		    inputStream.close();
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		try {
			// get an RSA cipher object and print the provider
	  		final Cipher cipher = Cipher.getInstance(ALGORITHM);
			// encrypt the plain text using the public key
			cipher.init(Cipher.ENCRYPT_MODE, key);
			cipherText = cipher.doFinal(text.getBytes());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return cipherText;
	}

  /**
   * Encrypt the plain text using public key.
   * 
   * @param text
   *          : original plain text
   * @param key
   *          :The public key
   * @return Encrypted text
   * @throws java.lang.Exception
   */
    public static byte[] encryptWithKey(String text, PublicKey key) {
	    byte[] cipherText = null;
	    try {
	      // get an RSA cipher object and print the provider
	      final Cipher cipher = Cipher.getInstance(ALGORITHM);
	      // encrypt the plain text using the public key
	      cipher.init(Cipher.ENCRYPT_MODE, key);
	      cipherText = cipher.doFinal(text.getBytes());
	    } catch (Exception e) {
	      e.printStackTrace();
	    }
	    return cipherText;
    }
  
    public static String decrypt(byte[] text) {
		byte[] dectyptedText = null;
		PrivateKey key = null;
		try {
			ObjectInputStream inputStream = null;
	
		    // Encrypt the string using the public key
		    inputStream = new ObjectInputStream(new FileInputStream(PRIVATE_KEY_FILE));
		    key = (PrivateKey) inputStream.readObject();
		    inputStream.close();
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		try {
			// get an RSA cipher object and print the provider
		    final Cipher cipher = Cipher.getInstance(ALGORITHM);

		    // decrypt the text using the private key
		    cipher.init(Cipher.DECRYPT_MODE, key);
		    dectyptedText = cipher.doFinal(text);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new String(dectyptedText);
    }

  /**
   * Decrypt text using private key.
   * 
   * @param text
   *          :encrypted text
   * @param key
   *          :The private key
   * @return plain text
   * @throws java.lang.Exception
   */
  public static String decryptWithKey(byte[] text, PrivateKey key) {
    byte[] dectyptedText = null;
    try {
      // get an RSA cipher object and print the provider
      final Cipher cipher = Cipher.getInstance(ALGORITHM);

      // decrypt the text using the private key
      cipher.init(Cipher.DECRYPT_MODE, key);
      dectyptedText = cipher.doFinal(text);

    } catch (Exception ex) {
      ex.printStackTrace();
    }

    return new String(dectyptedText);
  }

  /**
   * Test the EncryptionUntil
   */
  public static void main(String[] args) {

    try {

      // Check if the pair of keys are present else generate those.
      if (!areKeysPresent(PUBLIC_KEY_FILE, PRIVATE_KEY_FILE)) {
        // Method generates a pair of keys using the RSA algorithm and stores it
        // in their respective files
        generateKey(PUBLIC_KEY_FILE, PRIVATE_KEY_FILE);
      }

      final String originalText = "Text to be encrypted ";
      ObjectInputStream inputStream = null;

      // Encrypt the string using the public key
      inputStream = new ObjectInputStream(new FileInputStream(PUBLIC_KEY_FILE));
      final PublicKey publicKey = (PublicKey) inputStream.readObject();
      final byte[] cipherText = encryptWithKey(originalText, publicKey);
      inputStream.close();

      // Decrypt the cipher text using the private key.
      inputStream = new ObjectInputStream(new FileInputStream(PRIVATE_KEY_FILE));
      final PrivateKey privateKey = (PrivateKey) inputStream.readObject();
      final String plainText = decryptWithKey(cipherText, privateKey);

      // Printing the Original, Encrypted and Decrypted Text
      System.out.println("Original Text: " + originalText);
      System.out.println("Encrypted Text: " + new String(cipherText));
      System.out.println("Decrypted Text: " + plainText);

    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}