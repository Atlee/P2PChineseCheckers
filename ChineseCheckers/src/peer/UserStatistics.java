package peer;

import java.util.ArrayList;
import java.util.Date;

public class UserStatistics {
	
	private String uname;
	private Date dateRegistered;
	private Date dateLastLogin;
	
	private ArrayList<String> friends;
	private ArrayList<String> ignored;
	
	private int numWins;
	private int numLosses;
	private int numDraws;
	
	
	public UserStatistics( String username , Date currentDate) {
		uname = username;
		dateRegistered = currentDate;
		numWins = 0;
		numLosses = 0;
		numDraws = 0;
	}
	
	public String getUsername() {
		return uname;
	}
	
	public Date getDateRegistered() {
		return dateRegistered;
	}
	
	public void setLastLogin( Date lastLogin ) {
		dateLastLogin = lastLogin;
	}
	
	public Date getLastLogin() {
		return dateLastLogin;
	}
	
	public void addFriend( String username ) {
		if (!friends.contains(username)) {
			friends.add(username);
		}
	}
	
	public void removeFriend( String username ) {
		friends.remove(username);
	}
	
	public void ignoreUser( String username ) {
		if (!ignored.contains(username)) {
			ignored.add(username);
		}
	}

	public void unignoreUser( String username ) {
		ignored.remove(username);
	}
	
	public void incrWins() {
		numWins += 1;
	}
	
	public void incrLosses() {
		numLosses += 1;
	}
	
	public void incrDraws() {
		numDraws += 1;
	}
	
	public int getWins() {
		return numWins;
	}
	
	public int getLosses() {
		return numLosses;
	}
	
	public int getDraws() {
		return numDraws;
	}
	
	public double getWinLossRatio() {
		return ((double)numWins)/numLosses;
	}
	
	public int getGamesPlayed() {
		return numWins + numLosses + numDraws;
	}
	
	/* ------------------------------------------------------------------------------ */
	
	public static byte[] encrypt( UserStatistics stats, String password ) {
		//TODO
		return null;
	}

	public static UserStatistics decrypt( byte[] encryptedStats, String password ) {
		//TODO
		return null;
	}
	
}
