package test;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.security.Key;
import java.util.Arrays;

import utils.Constants;
import utils.EncryptUtils;

public class RAFTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws IOException {
		RandomAccessFile raf = new RandomAccessFile("file", "rw");
		raf.writeInt(1);
		raf.writeInt(2);
		raf.writeInt(3);
		raf.writeInt(4);
		raf.writeInt(5);
		
		raf.close();
		
		raf = new RandomAccessFile("file", "rw");
		System.out.println("Pointer: " + raf.getFilePointer());
		raf.readInt();
		System.out.println("Pointer: " + raf.getFilePointer());
		raf.readInt();
		System.out.println("Pointer: " + raf.getFilePointer());
		raf.seek(4);
		raf.writeInt(10);
		
		raf.seek(raf.length());
		raf.writeInt(100);
		System.out.println(raf.length());
		
		raf.seek(0);
		while (raf.getFilePointer() < raf.length()) {
			System.out.println(raf.readInt());
		}
		
		byte[] b = "10;11".getBytes();
		
		Key k = EncryptUtils.handleCreateSharedKey();
		
		byte[] ciphertext = EncryptUtils.encryptData(b, k, Constants.SHARED_ENCRYPT_ALG);
		System.out.println(ciphertext.length);

		b = "100000000000000000000000000000000000;111111111111111111111111111111111111".getBytes();
		
		ciphertext = EncryptUtils.encryptData(b, k, Constants.SHARED_ENCRYPT_ALG);
		System.out.println(ciphertext.length);
	}

}
