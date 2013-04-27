package hub;

import java.security.Key;
import java.security.PublicKey;
import java.util.Map;


/* Bundle of keys that need to be distributed to all players in a game. */
public class GameKeys {

	Key encryptKey;
	Map<String, PublicKey> signKeys;
	
	public GameKeys(Key encryptKey, Map<String, PublicKey> signKeys) {
		this.encryptKey = encryptKey;
		this.signKeys = signKeys;
	}
	
}
