package hub;

import java.net.InetAddress;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/* Monitor instantiated by the multi-threaded hub to track status of users
 * authenticated in the system.
 * @author Emma
 */
public class UserTracker {
	
	private SecureRandom sRand = new SecureRandom();
	
	// All users that are currently online
	private List<String> online = new ArrayList<String>();
	// Map: {username -> UserRecord}, contains a mapping for key u iff u in 'online'
	private Map<String, UserRecord> records = new HashMap<String, UserRecord>();

	public UserTracker() {
		// do initialization overhead now
		sRand.nextInt();
	}
	
	/* Add a username to the list of currently online users and return a fresh
	 * session secret for that user. Obviously, the hub should only call this after
	 * it has authenticated a user's login credentials!
	 */
	synchronized Integer add(String uname, InetAddress inetAddr) {
		if(online.contains(uname)) {
			records.remove(uname);
		} else {
			online.add(uname);
		}
		Integer secret = (Integer)sRand.nextInt();
		records.put(uname, new UserRecord(uname, inetAddr, secret));
		return secret;
	}
	
	/* Remove a username from the list of currently online users and invalidate
	 * the current session secret for that user (i.e. log him out of the system).
	 * Note: If uname is not currently online, nothing happens.
	 */
	synchronized void remove(String uname) {
		online.remove(uname);
		records.remove(uname);
	}
	
	/* Check whether a given secret matches the current session secret for a
	 * specified user. Return true iff uname is online and there is a match.
	 */
	synchronized boolean check(String uname, Integer secret) {
		Integer currentSecret = null;
		if(online.contains(uname)) {
			currentSecret = records.get(uname).sessionSecret;
		}
		return secret.equals(currentSecret);
	}
	
	/* Set the status of a specified user to 'active', which means that the user
	 * currently has an SSL connection open to the hub OR that the user is currently
	 * listed as a player in some active GameRecord (see GameTracker).
	 * Note: If uname is not currently online, nothing happens.
	 */
	synchronized void setActive(String uname) {
		if(online.contains(uname)) {
			records.get(uname).active = true;
		}
	}
	
	/* Set the status of a specified user to 'inactive', which means that the user
	 * does NOT currently have an SSL connection open to the hub AND that the user is
	 * NOT currently listed as a player in any active GameRecord (see GameTracker).
	 * */
	synchronized void setIdle(String uname) {
		if(online.contains(uname)) {
			UserRecord record = records.get(uname);
			record.lastContact = System.currentTimeMillis();
			record.active = false;
		}
	}
	
	/* Reap idle users (i.e. log them out of the system). An idle user is any user
	 * whose status is IDLE and whose time of last contact with the hub was more than
	 * 30 minutes ago. Return a list of the usernames of all reaped users.
	 */
	synchronized List<String> reapIdle() {
		List<String> reaped = new ArrayList<String>();
		for(int i=0; i < online.size(); i++) {
			String uname = online.get(i);
			UserRecord record = records.get(uname);
			if((!record.active) && (record.lastContact + 1800000 < System.currentTimeMillis())) {
				reaped.add(uname);
				records.remove(uname);
			}
		}
		for(int i=0; i < reaped.size(); i++) {
			online.remove(reaped.get(i));
		}
		return reaped;
	}
	
	/* Return the InetAddress of a specified user, or null if that user is not
	 * currently online.
	 */
	synchronized InetAddress getInetAddr(String uname) {
		InetAddress inetAddr = null;
		if(online.contains(uname)) {
			inetAddr = records.get(uname).inetAddr;
		}
		return inetAddr;
	}
	
	/* Return a list of the usernames of all currently online users. */
	synchronized List<String> listOnline() {
		return online;
	}

}
