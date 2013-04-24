package hub;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

public class StatisticsStore {

	private static final String STATS_FILE = "stats.txt";
	
	public void addStats(String user, byte[] blob) throws IOException {
		if (containsUser(user)) {
			System.out.println("user already in file");
			return;
		}
		
		File f = getStatsFile();
		RandomAccessFile raf = new RandomAccessFile(f, "rw");
		
		long nextEntryStart = raf.length() + StatsFileEntry.MAX_BLOB_SIZE - (raf.length() % StatsFileEntry.MAX_BLOB_SIZE);
		
		(new StatsFileEntry(user, blob)).writeToFile(raf, nextEntryStart);
		
		raf.close();
	}
	
	public byte[] getStats(String user) throws IOException {
		long userOffset = containsUserHelp(user);
		if (userOffset != -1) {
			File f = getStatsFile();
			RandomAccessFile raf = new RandomAccessFile(f, "rw");
			StatsFileEntry entry = StatsFileEntry.readEntryFromStream(raf, userOffset);
			return entry.blob;
		}
		return null;
	}
	
	public boolean containsUser(String user) throws IOException {
		long offset = containsUserHelp(user);
		
		if (offset != -1) {
			return true;
		}
		
		return false;
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
	
	public void replaceEntry(String user, byte[] blob) throws IOException {
		long userOffset = containsUserHelp(user);
		if (userOffset == -1) {
			addStats(user, blob);
		} else {
			File f = getStatsFile();
			RandomAccessFile raf = new RandomAccessFile(f, "rw");
			raf.seek(userOffset);
			new StatsFileEntry(user, blob).writeToFile(raf, userOffset);
			raf.close();
		}
	}
	
	public void removeUser(String user) throws IOException {
		long userOffset = containsUserHelp(user);
		if (userOffset == -1) {
			return;
		} else {
			File f = getStatsFile();
			RandomAccessFile raf = new RandomAccessFile(f, "rw");
			raf.seek(userOffset);
			new StatsFileEntry(" ", " ".getBytes()).writeToFile(raf, userOffset);
			raf.close();
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
	byte[] blob;
	
	public StatsFileEntry(String username, byte[] blob) {
		this.username = username;
		this.blob = blob;
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
		byte[] blob = new byte[len];
		for (int i = 0; i < len; i++) {
			blob[i] = raf.readByte();
		}
		return new StatsFileEntry(new String(usernameChars), blob);
	}
	
	public void writeToFile(RandomAccessFile raf, long nextEntryStart) throws IOException {
		if (blob == null || blob.length > MAX_BLOB_SIZE) {
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
		raf.writeInt(this.blob.length);
		raf.write(blob);
	}
}