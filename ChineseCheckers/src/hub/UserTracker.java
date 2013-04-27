package hub;

import java.net.InetAddress;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/* Monitor instantiated by the hub to track status of users authenticated in the system.
 * @author Emma
 */
public class UserTracker {
	
	private SecureRandom sRand = new SecureRandom();
	
	// The usernames of all users that are currently online
	private List<String> online = new ArrayList<String>();
	// Map: {username -> InetAddress}, contains a mapping for key u iff u in 'online'
	private Map<String, InetAddress> inetAddrs = new HashMap<String, InetAddress>();
	// Map: {username -> current session secret}, contains a mapping for key u iff u in 'online'
	private Map<String, Integer> sessionSecrets = new HashMap<String, Integer>();
	
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
			online.remove(uname);
			inetAddrs.remove(uname);
			sessionSecrets.remove(uname);
		}
		
		online.add(uname);
		inetAddrs.put(uname, inetAddr);
		
		Integer secret = (Integer)sRand.nextInt();
		sessionSecrets.put(uname, secret);
		return secret;
	}
	
	/* Remove a username from the list of currently online users and invalidate
	 * the current session secret for that user. This effectively logs a user out
	 * of the system, by removing access to hub services.
	 * Note: If uname is not currently online, nothing happens.
	 */
	synchronized void remove(String uname) {
		online.remove(uname);
		sessionSecrets.remove(uname);
		inetAddrs.remove(uname);
	}
	
	/* Check whether a given secret matches the current session secret for a
	 * specified user. Return True iff uname is online and there is a match.
	 */
	synchronized boolean check(String uname, Integer secret) {
		Integer currentSecret = sessionSecrets.get(uname);
		return secret.equals(currentSecret);
	}
	
	/* Return the InetAddress of the machine that a specified user logged in
	 * from. If uname is not currently online, return null.
	 */
	synchronized InetAddress getInetAddr(String uname) {
		if(online.contains(uname)) {
			return inetAddrs.get(uname);
		} else {
			return null;
		}
	}
	
	/* Return a list of the usernames of all currently online users. */
	synchronized List<String> list() {
		return online;
	}

}
