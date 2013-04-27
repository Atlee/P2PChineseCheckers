package test.GameTest;

import java.awt.Point;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import game.AuditLog;
import game.Move;
import game.Player;
import game.Board;
import game.Rules;

public class MoveSerializeTest {
	
	static String log1 = null;
	static String log2 = null;
	static boolean write1 = false;
	static boolean write2 = false;
	static boolean write3 = false;
	static final Lock lock = new ReentrantLock();
	static final Condition notDone = lock.newCondition();
	static final Lock threadLock = new ReentrantLock();
	static final Semaphore threadWrite = new Semaphore(0);
	
	public static void main(String[] argv) throws InterruptedException {
		lock.lock();
		try {
			new Thread(new Host(4444)).start();
			new Thread(new Peer(4444)).start();
			while (log1 == null || log2 == null) {
				System.out.println("waiting for threads to finish");
				notDone.await();
			}
			System.out.println(log1.equals(log2));
		} finally {
			lock.unlock();
		}
	}
	
	public static void setLog1(String log) {
		lock.lock();
		try {
			log1 = log;
			notDone.signal();
		} finally {
			lock.unlock();
		}
	}
	
	public static void setLog2(String log) {
		lock.lock();
		try {
			log2 = log;
			notDone.signal();
		} finally {
			lock.unlock();
		}
	}
	
	public void test() {
		String log = "";
		Player p1 = new Player("p1", 0);
		Player p2 = new Player("p2", 1);
		
		Move m1 = new Move(p1, new Point(3,4));
		m1.add(new Point(4,4));
		
		Move m2 = new Move(p2, new Point(13, 4));
		m2.add(new Point(12, 4));
		
		Move m3 = new Move(p1, new Point(1,5));
		m3.add(new Point(3,4));
		m3.add(new Point(5,3));
		
		log += m1.serialize() + "\n";
		log += m2.serialize() + "\n";
		log += m3.serialize() + "\n";
		System.out.println(log);
	}
}

class Host implements Runnable {
	
	int port;
	
	public Host(int port) {
		this.port = port;
	}

	@Override
	public void run() {
		AuditLog log = null;
		Socket peer = null;
		try {
			ServerSocket ss = new ServerSocket(port);
			log = new AuditLog();
			
			peer = ss.accept();
			System.out.println("Socket accepted");
			ss.close();
		} catch (IOException e) {
			;
		}
		
		MoveSerializeTest.threadLock.lock();
		try {
			ObjectOutputStream out = new ObjectOutputStream(peer.getOutputStream());
			
			Player p1 = new Player("p1", 0);
			
			//perform first move
			Move m1 = new Move(p1, new Point(3,4));
			m1.add(new Point(4,4));
			String m1s = m1.serialize();
			log.append(m1s);
			
			out.writeUTF(m1s);
			MoveSerializeTest.write2 = true;
			MoveSerializeTest.threadWrite.notify();
			System.out.println("wrote first move");
			
			while(!MoveSerializeTest.write1) {
				MoveSerializeTest.threadWrite.await();
			}
			
			ObjectInputStream in = new ObjectInputStream(peer.getInputStream());
			Move m2 = Move.deSerialize(in.readUTF());
			System.out.println("read second move");
			log.append(m2.serialize());
			
			//perform second move
			Move m3 = new Move(p1, new Point(1,5));
			m3.add(new Point(3,4));
			m3.add(new Point(5,3));
			String m3s = m3.serialize();
			log.append(m3s);
			
			out.writeUTF(m3s);
			
			MoveSerializeTest.write2 = true;
			MoveSerializeTest.threadWrite.notify();
			
			System.out.println("SettingLog");
			MoveSerializeTest.setLog1(log.getLog());
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			MoveSerializeTest.threadLock.unlock();
		}
	}
}

class Peer implements Runnable {
	
	int port;
	
	public Peer(int port) {
		this.port = port;
	}

	@Override
	public void run() {
		Socket peer = null;
		try {
			peer = new Socket(InetAddress.getLocalHost(), port);
		} catch (UnknownHostException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		AuditLog log = new AuditLog();
		MoveSerializeTest.threadLock.lock();
		try {
			
			Player p2 = new Player("p2", 1);
			
			while (!MoveSerializeTest.write2) {
				MoveSerializeTest.threadWrite.await();
			}
			
			ObjectInputStream in = new ObjectInputStream(peer.getInputStream());
			System.out.println("Before read");
			Move m1 = Move.deSerialize(in.readUTF());
			System.out.println("Thread 2: read first move");
			log.append(m1.serialize());
			
			ObjectOutputStream out = new ObjectOutputStream(peer.getOutputStream());
			Move m2 = new Move(p2, new Point(13,4));
			m1.add(new Point(12,4));
			String m2s = m2.serialize();
			log.append(m2s);
			
			out.writeUTF(m2s);
			MoveSerializeTest.write1 = true;
			MoveSerializeTest.threadWrite.notify();
			System.out.println("Thread 2: wrote second move");
			
			while (!MoveSerializeTest.write3) {
				MoveSerializeTest.threadWrite.await();
			}
			
			Move m3 = Move.deSerialize(in.readUTF());
			System.out.println("Thread 2: read third move");
			log.append(m3.serialize());
			
			peer.close();
			
			MoveSerializeTest.setLog2(log.getLog());
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			MoveSerializeTest.threadLock.unlock();
		}
	}
	
}

