package hub;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class StatisticsStore {

	private static final String STATS_FILE = "stats.txt";
	private Lock lock = new ReentrantLock();
	
	public void addStats(String user, int[] stats) throws IOException {
		lock.lock();
		try {
			if (containsUser(user)) {
				System.out.println("user already in file");
				return;
			}
			
			File f = getStatsFile();
			RandomAccessFile raf = new RandomAccessFile(f, "rw");
			
			long emptyIndex = containsUserHelp(" ");
			long nextEntryStart = raf.length() + StatsFileEntry.MAX_BLOB_SIZE - (raf.length() % StatsFileEntry.MAX_BLOB_SIZE);
			if (emptyIndex != -1) {
				nextEntryStart = emptyIndex;
			}
			
			(new StatsFileEntry(user, stats)).writeToFile(raf, nextEntryStart);
			
			raf.close();
		} finally {
			lock.unlock();
		}
	}
	
	public int[] getStats(String user) throws IOException {
		int[] output = null;
		lock.lock();
		try {
			long userOffset = containsUserHelp(user);
			if (userOffset != -1) {
				File f = getStatsFile();
				RandomAccessFile raf = new RandomAccessFile(f, "rw");
				StatsFileEntry entry = StatsFileEntry.readEntryFromStream(raf, userOffset);
				output = entry.stats;
			}
		} finally {
			lock.unlock();
		}
		return output;
	}
	
	public boolean containsUser(String user) throws IOException {
		boolean output = false;
		lock.lock();
		try {
			long offset = containsUserHelp(user);
			
			if (offset != -1) {
				output = true;
			} else {
				output = false;
			}
		} finally {
			lock.unlock();
		}
		return output;
	}
	
	private long containsUserHelp(String user) throws IOException {
		File f = getStatsFile();
		RandomAccessFile raf = new RandomAccessFile(f, "r");
		
		while(raf.getFilePointer() < raf.length()) {
			long entryStart = raf.getFilePointer();
			
			StatsFileEntry entry = StatsFileEntry.readEntryFromStream(raf, entryStart);
			if (entry.equals(user)) {
				raf.close();
				return entryStart;
			}
			
			long nextPointer = entryStart + StatsFileEntry.MAX_BLOB_SIZE;
			raf.seek(nextPointer);
		}
		raf.close();		
		return -1;
	}
	
	public void replaceEntry(String user, int[] stats) throws IOException {
		lock.lock();
		try {
			long userOffset = containsUserHelp(user);
			if (userOffset == -1) {
				addStats(user, stats);
			} else {
				File f = getStatsFile();
				RandomAccessFile raf = new RandomAccessFile(f, "rw");
				raf.seek(userOffset);
				new StatsFileEntry(user, stats).writeToFile(raf, userOffset);
				raf.close();
			}
		} finally {
			lock.unlock();
		}
	}
	
	public void removeUser(String user) throws IOException {
		lock.lock();
		try {
			long userOffset = containsUserHelp(user);
			if (userOffset == -1) {
				return;
			} else {
				File f = getStatsFile();
				RandomAccessFile raf = new RandomAccessFile(f, "rw");
				raf.seek(userOffset);
				new StatsFileEntry(" ", new int[0]).writeToFile(raf, userOffset);
				raf.close();
			}
		} finally {
			lock.unlock();
		}
	}
	
	private File getStatsFile() throws IOException {
		File f = new File(STATS_FILE);
		if (!f.exists()) {
			f.createNewFile();
		}
		return f;
	}
}

class StatsFileEntry {
	
	public static final int MAX_BLOB_SIZE = 512;
	String username;
	int[] stats;
	
	public StatsFileEntry(String username, int[] stats) {
		this.username = username;
		this.stats = stats;
	}
	
	public boolean equals(String username) {
		return this.username.equals(username);
	}
	
	public static StatsFileEntry readEntryFromStream(RandomAccessFile raf, long entryStart) 
			throws IOException {
		if (raf == null) {
			throw new IOException("raf null");
		}
		
		if (entryStart < 0 || entryStart % MAX_BLOB_SIZE != 0) {
			throw new IOException("Invalid offset");
		}
		
		raf.seek(entryStart);
		
		int len = raf.readInt();
		char[] usernameChars = new char[len];
		for (int i = 0; i < len; i++) {
			usernameChars[i] = raf.readChar();
		}
		len = raf.readInt();
		int[] stats = new int[len];
		for (int i = 0; i < len; i++) {
			stats[i] = raf.readInt();
		}
		return new StatsFileEntry(new String(usernameChars), stats);
	}
	
	public void writeToFile(RandomAccessFile raf, long nextEntryStart) throws IOException {
		if (stats == null || (Integer.SIZE * 2 + username.length() + (Integer.SIZE * stats.length)) > MAX_BLOB_SIZE) {
			//if the stats are null or the size of the stats plus the size of hte usernaem
			//is greater than the maximum allowed size
			throw new IOException("Invalid data blob"); 
		}
		
		if (nextEntryStart < 0 || nextEntryStart % MAX_BLOB_SIZE != 0) {
			throw new IOException("Invalid offset");
		}
		
		if (raf == null) {
			throw new IOException("file null");
		}
		raf.seek(nextEntryStart);
		
		raf.writeInt(username.length());
		raf.writeChars(username);
		raf.writeInt(this.stats.length);
		for (int i = 0; i < this.stats.length; i++) {
			raf.writeInt(this.stats[i]);
		}
	}
}