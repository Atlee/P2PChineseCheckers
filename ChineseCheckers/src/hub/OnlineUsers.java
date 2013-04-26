package hub;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class OnlineUsers {
	
	private List<String> unames = new ArrayList<String>();
	private Map<String, Integer> sessionSecrets = new HashMap<String, Integer>();
	
	private SecureRandom sRand = new SecureRandom();
	
	public OnlineUsers() {
		sRand.nextInt();
	}
	
	synchronized Integer add(String uname) {
		if(unames.contains(uname)) {
			unames.remove(uname);
			sessionSecrets.remove(uname);
		}
		
		unames.add(uname);
		
		Integer secret = (Integer)sRand.nextInt();
		sessionSecrets.put(uname, secret);
		return secret;
	}
	
	synchronized void remove(String uname) {
		unames.remove(uname);
		sessionSecrets.remove(uname);
	}
	
	synchronized boolean check(String uname, Integer secret) {
		Integer currentSecret = sessionSecrets.get(uname);
		return secret.equals(currentSecret);
	}
	
	synchronized List<String> list() {
		return unames;
	}

}
