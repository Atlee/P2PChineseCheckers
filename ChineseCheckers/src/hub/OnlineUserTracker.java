package hub;

import java.net.InetAddress;
import java.security.SecureRandom;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;


/* Monitor instantiated by the multi-threaded hub to track status of users
 * currently authenticated in the system.
 * @author Emma
 */
public class OnlineUserTracker {
	
	private SecureRandom sRand = new SecureRandom();
	
	// All users that are currently online
	private List<String> online = new ArrayList<String>();
	// Map: {username -> OnlineUserRecord}, contains a mapping for key u iff u in 'online'
	private Map<String, OnlineUserRecord> records = new HashMap<String, OnlineUserRecord>();

	public OnlineUserTracker() {
		// do initialization overhead now
		sRand.nextInt();
	}
	
	/* Add a username to the list of currently online users and return a fresh
	 * random session ID for that user. Obviously, the hub should only call this after
	 * it has authenticated a user's login credentials!
	 */
	synchronized Integer add(String uname, InetAddress inetAddr) {
		if(online.contains(uname)) {
			records.remove(uname);
		} else {
			online.add(uname);
		}
		Integer sessionID = (Integer)sRand.nextInt();
		records.put(uname, new OnlineUserRecord(uname, inetAddr, sessionID));
		return sessionID;
	}
	
	/* Remove a username from the list of currently online users and invalidate
	 * the current session ID for that user (i.e. log him out of the system).
	 * Note: If uname is not currently online, nothing happens.
	 */
	synchronized void remove(String uname) {
		online.remove(uname);
		records.remove(uname);
	}
	
	/* Check whether the given ID matches the current, valid session ID for a 
	 * specified specified user. Return true iff that user is online and there is 
	 * a session ID match.
	 */
	synchronized boolean check(String uname, Integer sessionID) {
		Integer currentID = null;
		if(online.contains(uname)) {
			currentID = records.get(uname).sessionID;
		}
		return sessionID.equals(currentID);
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
	
	/* Set the status of a specified user to 'active', which means that the user
	 * currently has an SSL connection open to the hub OR that the user is currently
	 * listed as a player in some active GameRecord (see GameTracker).
	 * Note: If uname is not currently online, nothing happens.
	 */
	synchronized void setActive(String uname, Integer gameID) {
		if(online.contains(uname)) {
			OnlineUserRecord record = records.get(uname);
			record.active = true;
			record.inGame = gameID;
		}
	}
	
	/* Set the status of a specified user to 'inactive', which means that the user
	 * does NOT currently have an SSL connection open to the hub AND that the user is
	 * NOT currently listed as a player in any active GameRecord (see GameTracker).
	 * Note: If uname is not currently online, nothing happens.
	 * */
	synchronized void setIdle(String uname) {
		if(online.contains(uname)) {
			OnlineUserRecord record = records.get(uname);
			record.inGame = null;
			record.active = false;
			record.lastContact = System.currentTimeMillis();
		}
	}
	
	/* Reap idle users (i.e. log them out of the system). An idle user is any user
	 * whose status is inactive and whose time of last contact with the hub was more
	 * than 30 minutes ago.
	 */
	synchronized void reapIdleUsers() {
		List<String> reaped = new ArrayList<String>();
		for(String uname : online) {
			OnlineUserRecord record = records.get(uname);
			if((!record.active) && (record.lastContact + 1800000 < System.currentTimeMillis())) {
				reaped.add(uname);
				records.remove(uname);
			}
		}
		for(String uname : reaped) {
			online.remove(uname);
		}
	}
	
	/* Return a map {username -> session ID} for all currently online users. */
	synchronized Map<String, Integer> listOnlineUsers() {
		Map<String, Integer> online = new HashMap<String, Integer>();
		for(String uname : this.online) {
			online.put(uname, records.get(uname).sessionID);
		}
		return online;
	}

}
