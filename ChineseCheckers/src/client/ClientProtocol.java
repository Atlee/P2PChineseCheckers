package client;

/** A ClientProtocol is a sequence of responses to a server
 * 
 * @author Atlee
 *
 */

public class ClientProtocol {
	
	/** a simple response, just replies hello server regardless of the server's
	 * response
	 * @param s
	 * @return
	 */
	public String processInput(String s) {
		return "Hello Server";
	}
}
