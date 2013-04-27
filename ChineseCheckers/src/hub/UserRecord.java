package hub;

import java.net.InetAddress;


/* The UserTracker monitor instantiates these to track individual online users.
 * @author Emma
 */
public class UserRecord {
	
	final String userName;
	final InetAddress inetAddr;
	final int sessionSecret;
	
	boolean active;
	long lastContact;
	
	public UserRecord( String userName, InetAddress inetAddr, int sessionSecret ) {
		this.userName = userName;
		this.inetAddr = inetAddr;
		this.sessionSecret = sessionSecret;
		this.active = true;
		this.lastContact = System.currentTimeMillis();
	}

}
