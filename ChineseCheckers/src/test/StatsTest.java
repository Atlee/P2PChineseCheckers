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
		int[] blob1 = {1000000000, 200000000};
		int[] blob2 = {1, 2};
		
		ss.addStats("user1", blob1);
		System.out.println(ss.containsUser("user1") + ": " + "should be true");
		System.out.println(Arrays.equals(blob1, ss.getStats("user1")));
		ss.addStats("user2", blob1);
		System.out.println(Arrays.equals(blob1, ss.getStats("user2")));
		ss.addStats("user3", blob1);
		ss.addStats("user4", blob1);
		System.out.println(Arrays.equals(blob1, ss.getStats("user1")));
		ss.replaceEntry("user1", blob2);
		System.out.println(Arrays.equals(blob2, ss.getStats("user1")));
		ss.replaceEntry("user2", blob1);
		System.out.println(Arrays.equals(blob1, ss.getStats("user2")));
		
		
		ss.removeUser("user1");
		System.out.print(!ss.containsUser("user1"));
	}

}
