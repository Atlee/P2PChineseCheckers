package utils;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.security.KeyPairGenerator;
import java.security.KeyPair;

public class GenerateKeys {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		
		KeyPairGenerator keyGen = KeyPairGenerator.getInstance(Constants.KEYGEN_ALGORITHM);
		//TODO:add secure random input here
		keyGen.initialize(1024);
		KeyPair keys = keyGen.generateKeyPair();
		
		ObjectOutputStream publicKeyFile = new ObjectOutputStream(new FileOutputStream("public.key"));
		ObjectOutputStream privateKeyFile = new ObjectOutputStream(new FileOutputStream("private.key"));
		
		
		publicKeyFile.writeObject(keys.getPublic());
		privateKeyFile.writeObject(keys.getPrivate());
		
		publicKeyFile.close();
		privateKeyFile.close();

	}

}
