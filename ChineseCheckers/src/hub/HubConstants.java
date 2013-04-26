package hub;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.security.PrivateKey;
import java.security.PublicKey;

import utils.Constants;

public class HubConstants {
	
	private static final String HUB_PRIVATE_KEY_FILE = "private.key";
	private static PrivateKey hubPrivate = null;
	
	public static void privateKeyInit() {
		if (hubPrivate == null) {
			ObjectInputStream in = null;
			try {
				in = new ObjectInputStream(new FileInputStream(HUB_PRIVATE_KEY_FILE));
				hubPrivate = (PrivateKey) in.readObject();
				in.close();
			} catch (IOException | ClassNotFoundException e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
	}
	
	public static PublicKey getHubPublic() {
		return Constants.getHubPublicKey();
	}
	
	public static PrivateKey getHubPrivate() {
		if (hubPrivate == null) {
			privateKeyInit();
		}
		
		return hubPrivate;
	}	
}
