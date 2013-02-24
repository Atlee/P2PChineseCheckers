package protocols;

import java.net.Socket;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

import utils.SignUtils;

/** A ClientProtocol is a sequence of responses to a server
 * 
 * @author Atlee
 *
 */

public class ClientProtocol extends Protocol {
	
	private static final byte[] PUBLIC_KEY_HUB = new byte[] {
		(byte) 0x30, (byte) 0x81, (byte) 0x9f, (byte) 0x30, (byte) 0x0d, (byte) 0x06, (byte) 0x09, (byte) 0x2a, (byte) 0x86, (byte) 0x48, (byte) 0x86, (byte) 0xf7, (byte) 0x0d, (byte) 0x01, (byte) 0x01, (byte) 0x01, (byte) 0x05, (byte) 0x00, (byte) 0x03, (byte) 0x81, (byte) 0x8d, (byte) 0x00, (byte) 0x30, (byte) 0x81, (byte) 0x89, (byte) 0x02, (byte) 0x81, (byte) 0x81, (byte) 0x00, (byte) 0xa0, (byte) 0x02, (byte) 0x9c, (byte) 0xd6, (byte) 0x03, (byte) 0x2d, (byte) 0x6f, (byte) 0xf1, (byte) 0xd6, (byte) 0x83, (byte) 0x8c, (byte) 0xe4, (byte) 0xb6, (byte) 0xec, (byte) 0xee, (byte) 0x9c, (byte) 0x90, (byte) 0x44, (byte) 0x88, (byte) 0x69, (byte) 0xfe, (byte) 0xd0, (byte) 0x0e, (byte) 0xa7, (byte) 0x69, (byte) 0x1f, (byte) 0x98, (byte) 0x82, (byte) 0xf5, (byte) 0x2d, (byte) 0x8c, (byte) 0xae, (byte) 0x25, (byte) 0x7b, (byte) 0x81, (byte) 0xe2, (byte) 0x9f, (byte) 0xea, (byte) 0x9a, (byte) 0xb3, (byte) 0xd8, (byte) 0x46, (byte) 0x31, (byte) 0x62, (byte) 0x0b, (byte) 0xf7, (byte) 0xef, (byte) 0x63, (byte) 0x11, (byte) 0x83, (byte) 0xf3, (byte) 0x02, (byte) 0x53, (byte) 0x1d, (byte) 0x52, (byte) 0x38, (byte) 0xb6, (byte) 0xcb, (byte) 0x79, (byte) 0x9c, (byte) 0x0a, (byte) 0xe4, (byte) 0x09, (byte) 0xfe, (byte) 0xd9, (byte) 0x36, (byte) 0x0a, (byte) 0x6c, (byte) 0xb1, (byte) 0xa1, (byte) 0x1a, (byte) 0xb2, (byte) 0xa4, (byte) 0x74, (byte) 0x5a, (byte) 0x9a, (byte) 0x12, (byte) 0x22, (byte) 0xb4, (byte) 0x0d, (byte) 0x9e, (byte) 0x19, (byte) 0x59, (byte) 0x75, (byte) 0xb4, (byte) 0x79, (byte) 0xb8, (byte) 0xc7, (byte) 0x2d, (byte) 0x40, (byte) 0x09, (byte) 0x69, (byte) 0x11, (byte) 0x46, (byte) 0x0c, (byte) 0x5f, (byte) 0xb5, (byte) 0x1d, (byte) 0x27, (byte) 0x47, (byte) 0x5c, (byte) 0xb9, (byte) 0x9a, (byte) 0x31, (byte) 0xab, (byte) 0x1a, (byte) 0xf7, (byte) 0xd7, (byte) 0xdc, (byte) 0xa4, (byte) 0x01, (byte) 0xa2, (byte) 0xb7, (byte) 0x69, (byte) 0x9a, (byte) 0xef, (byte) 0x46, (byte) 0x9a, (byte) 0xa9, (byte) 0x60, (byte) 0x5d, (byte) 0xd6, (byte) 0x48, (byte) 0x2f, (byte) 0x43, (byte) 0xa2, (byte) 0x15, (byte) 0x65, (byte) 0x02, (byte) 0x03, (byte) 0x01, (byte) 0x00, (byte) 0x01
	};
	private static PublicKey publicKeyHub = null;
	protected static boolean isInit = false;
	
	public static void init() {
		try {
			KeyFactory kf = KeyFactory.getInstance(SignUtils.KEYGEN_ALGORITHM);
			X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(PUBLIC_KEY_HUB);
			
			publicKeyHub = kf.generatePublic(pubKeySpec);
			ClientProtocol.isInit = true;
		} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
			// TODO Auto-generated catch block
			System.out.println("publicKey recovery failed");
			System.exit(1);
		}
	}
	
	public static PublicKey getHubKey() {
		if (!isInit) {
			init();
		}
		return publicKeyHub;
	}
	
	/** a simple response, just replies hello server regardless of the server's
	 * response
	 * @param s
	 * @return
	 */
	public String processInput(String s) {
		return "Default Protocol";
	}
	
	public void execute(Socket s) {
		;
	}
}
