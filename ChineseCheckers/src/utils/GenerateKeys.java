package utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.security.KeyPairGenerator;
import java.security.KeyPair;
import java.security.SecureRandom;

public class GenerateKeys {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		
		KeyPairGenerator keyGen = KeyPairGenerator.getInstance(Constants.KEYGEN_ALGORITHM);
		SecureRandom rand = SecureRandom.getInstance(Constants.RANDOM_ALGORITHM);
		rand.setSeed("Hub".getBytes());
		keyGen.initialize(1024, rand);
		KeyPair keys = keyGen.generateKeyPair();
		
		FileOutputStream publicKeyFile = (new FileOutputStream("public.key"));
		FileOutputStream privateKeyFile = (new FileOutputStream("private.key"));
		
		ObjectOutputStream publicKeyObjOut = new ObjectOutputStream(publicKeyFile);
		ObjectOutputStream privateKeyObjOut = new ObjectOutputStream(privateKeyFile);
		
		PrintWriter publicPW = new PrintWriter(new File("publicBytes.key"));
		PrintWriter privatePW = new PrintWriter(new File("privateBytes.key"));
		
		byte[] publicKeyBytes = keys.getPublic().getEncoded();
		byte[] privateKeyBytes = keys.getPrivate().getEncoded();
		
		publicKeyObjOut.writeObject(keys.getPublic());
		privateKeyObjOut.writeObject(keys.getPrivate());
		
		publicKeyFile.close();
		privateKeyFile.close();
		
		publicKeyObjOut.close();
		privateKeyObjOut.close();

	}
}
