package test;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;

import hub.PasswordStore;


public class PasswordStoreTest {
	
	public static void main(String[] argv) {
		PasswordStore pws = new PasswordStore();
		pws.addEntry("a", "a".toCharArray());
		pws.addEntry("b", "b".toCharArray());
		pws.addEntry("10user3", "b".toCharArray());
		
		System.out.println("All should be true");
		try {
			System.out.println(pws.containsEntry("a"));
			System.out.println(pws.containsEntry("b"));
			System.out.println(!pws.containsEntry("godzilla"));
			System.out.println(pws.authenticate("a", "a".toCharArray()));
			System.out.println(!pws.authenticate("b", "pass3".toCharArray()));
			pws.replaceEntry("a", "newa".toCharArray());
			System.out.println(pws.authenticate("a", "newa".toCharArray()));
			System.out.println(pws.removeEntry("a"));
			System.out.println(pws.addEntry("user4", "pass4".toCharArray()));
			System.out.println(pws.authenticate("10user3", "b".toCharArray()));
			System.out.println(!pws.authenticate("b", "pass3".toCharArray()));
			System.out.println(pws.authenticate("user4", "pass4".toCharArray()));
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
