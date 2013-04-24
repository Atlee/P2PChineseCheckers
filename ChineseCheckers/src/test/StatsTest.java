package test;

import java.io.IOException;
import java.security.Key;
import java.util.Arrays;

import utils.Constants;
import utils.EncryptUtils;
import hub.StatisticsStore;

public class StatsTest {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		StatisticsStore ss = new StatisticsStore();
		String blob1 = "100000:2000000";
		String blob2 = "1:2";
		Key k = EncryptUtils.handleCreateSharedKey();
		byte[] ciphertext = EncryptUtils.encryptData(blob1.getBytes(), k, Constants.SHARED_ENCRYPT_ALG);
		byte[] ciphertext2 = EncryptUtils.encryptData(blob2.getBytes(), k, Constants.SHARED_ENCRYPT_ALG);
		ss.addStats("user1", ciphertext);
		System.out.println(ss.containsUser("user1") + ": " + "should be true");
		System.out.println(Arrays.equals(ciphertext, ss.getStats("user1")));
		ss.addStats("user2", ciphertext);
		System.out.println(Arrays.equals(ciphertext, ss.getStats("user2")));
		ss.addStats("user3", ciphertext);
		ss.addStats("user4", ciphertext);
		System.out.println(Arrays.equals(ciphertext, ss.getStats("user1")));
		ss.replaceEntry("user1", ciphertext2);
		System.out.println(Arrays.equals(ciphertext2, ss.getStats("user1")));
		ss.replaceEntry("user2", ciphertext);
		System.out.println(Arrays.equals(ciphertext, ss.getStats("user2")));
		
		
		ss.removeUser("user1");
		System.out.print(ss.containsUser("user1"));
	}

}
