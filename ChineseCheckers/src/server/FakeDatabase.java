package server;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Scanner;

/** a 'database' for stable storage of key/value pairs
 * 
 * @author Atlee
 *
 */
public class FakeDatabase {
	
	static final private String DATABASE_FILE = 
			"DefinitelyNotPasswordStuff.txt";
	
	HashMap<String, String> KVPairs = new HashMap<String, String>();
	
	/** File format of string is 
	 * key:value
	 */
	public void init() {
		try {
			File f = new File(DATABASE_FILE);
			Scanner scn = new Scanner(f);
			while (scn.hasNextLine()) {
				String line = scn.nextLine();
				System.out.println(line);
				String[] keyValue = line.split(":");
				KVPairs.put(keyValue[0], keyValue[1]);
			}
			scn.close();
		} catch (FileNotFoundException e) {
			// if the file doesn't exist, then we are done initializing
		}
	}
	
	public String getValue(String key) {
		String output = null;
		if (KVPairs.containsKey(key)) {
			output = KVPairs.get(key);
		}
		return output;
	}
	
	public void setValue(String key, String value) {
		KVPairs.put(key,  value);
		try {
			PrintWriter out = new PrintWriter(
					new BufferedWriter(new FileWriter(DATABASE_FILE, true)));
			out.println(key + ":" + value);
			out.close();
		} catch (IOException e) {
			System.out.println("Database write failed");
		}
	}
	
	public void clearDatabase() {
		if (ServerMain.DEBUG) {
			File f = new File(DATABASE_FILE);
			f.delete();
		}
	}
}