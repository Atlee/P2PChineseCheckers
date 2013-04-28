package hub;

import java.net.InetAddress;


/* The UserTracker monitor instantiates these to track individual online users. A brand 
 * new UserRecord is created for each login session (i.e. this info is not persistent). 
 * @author Emma
 */
public class OnlineUserRecord {
	
	final String userName;
	final InetAddress inetAddr;
	final int sessionID;
	
	boolean active = true; // true iff currently open SSL connection to hub OR inGame != null
	Integer inGame = null; // ID of game this user is currently a player in, if any
	long lastContact = System.currentTimeMillis(); // approx last time that active == true
	
	public OnlineUserRecord( String userName, InetAddress inetAddr, int sessionID ) {
		this.userName = userName;
		this.inetAddr = inetAddr;
		this.sessionID = sessionID;
	}

}
