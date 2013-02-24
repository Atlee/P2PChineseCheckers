package server;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.PublicKey;
import java.util.HashMap;

/** a 'database' for stable storage of key/value pairs
 * 
 * @author Atlee
 *
 */
public class FakeDatabase {
	
	static final private String DATABASE_FILE = 
			"DefinitelyNotPasswordStuff.txt";
	
	private HashMap<String, PublicKey> KVPairs = new HashMap<String, PublicKey>();
	private boolean isInit = false;
	
	/** File format of string is 
	 * key:value
	 */
	public void init() {
		try {
			File f = new File(DATABASE_FILE);
			FileInputStream fin = new FileInputStream(f);
			try {
				while (fin.available() > 0) {
					BufferedReader rd = new BufferedReader(
							new InputStreamReader(fin));
					String key = rd.readLine();
					ObjectInputStream objIn= new ObjectInputStream(fin);
					PublicKey pubKey = (PublicKey) objIn.readObject();
					KVPairs.put(key, pubKey);
				}
				fin.close();
			} catch (IOException | ClassNotFoundException e) {
				e.printStackTrace();
				System.exit(1);
			}
			isInit = true;
		} catch (FileNotFoundException e) {
			// if the file doesn't exist, then we are done initializing
		}
	}
	
	public PublicKey getValue(String key) {
		if (!isInit) {
			this.init();
		}
		
		PublicKey output = null;
		if (KVPairs.containsKey(key)) {
			output = KVPairs.get(key);
		}
		return output;
	}
	
	public void setValue(String key, PublicKey value) {
		if (!isInit) {
			this.init();
		}
		
		if (!KVPairs.containsKey(key)) {
			KVPairs.put(key,  value);
			try {
				FileOutputStream fout = new FileOutputStream(DATABASE_FILE, true);
				DataOutputStream out = new DataOutputStream(fout);
				out.writeChars(key + "\n");
				ObjectOutputStream objOut = new ObjectOutputStream(fout);
				objOut.writeObject(value);
				out.close();
			} catch (IOException e) {
				System.out.println("Database write failed");
			}
		}
	}
	
	public void clearDatabase() {
		if (!isInit) {
			this.init();
		}
		
		File f = new File(DATABASE_FILE);
		f.delete();
	}
	
	public boolean containsKey(String key) {
		if (!isInit) {
			this.init();
		}
		return KVPairs.containsKey(key);
	}
}