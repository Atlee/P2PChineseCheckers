package game;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.net.SocketException;

public class Game implements Runnable{
	//order of players in these lists are important
	//These lists should be coming from the hub, therefore everyone will
	//receive the same order list
	private final ArrayList<Player> players;
	private Board board;
	private int rotationIndex = 0;
	private Player localPlayer;
	private Interaction communication;
	private Scanner scn = new Scanner(System.in);
	
	//test
	private ArrayList<Point> points = new ArrayList<Point>();
	private Move point;
	
	public Game(List<Player> _players, Player _localPlayer, Interaction _communication) throws Exception {
		players = extractPlayers(_players);
		board = new Board(this);
		localPlayer = _localPlayer;
		communication = _communication;
		
		//test
		points.add(new Point(0,0));
		point = new Move(getPlayer(0), points);
		
		Thread t = new Thread(this);
		t.start();
	}

	@Override
	public void run() {
		try {
			while (!Rules.gameOver(this)) {
				board.printBoard();
				Move m;
				if (this.localPlayersTurn()) {
					m = getMove();
					if (m.equals(point)) { 
						//test
						break;
					}
					while (!Rules.checkMove(localPlayer, board, m)) {
						//prompt invalid
						m = getMove();
					}
					communication.shareMove(m);
				} else {
					m = communication.waitForOpponent();
				}
				updateBoard(m);
				
				rotationIndex++;
			}
			board.printBoard();
			System.out.println("Winner!");
			communication.endGame(getPlayer(0), getPlayer(rotationIndex % players.size()));
		} catch (SocketException e) {
			System.out.println("Lost connection to other player");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/*private Move getMove() {
		//test
		if (rotationIndex == 0) {
			ArrayList<Point> p = new ArrayList<Point>();
			p.add(new Point(3, 4));
			p.add(new Point(4, 4));
			Move m = new Move(players.get(0), p);
			return m;
		}
		
		if (rotationIndex == players.size()) {
			ArrayList<Point> p = new ArrayList<Point>();
			p.add(new Point(1, 5));
			p.add(new Point(3, 4));
			p.add(new Point(5, 3));
			Move m = new Move(players.get(0), p);
		
			return m;
		}
		
		if (rotationIndex == 2 * players.size()) {
			ArrayList<Point> p = new ArrayList<Point>();
			p.add(new Point(2, 6));
			p.add(new Point(4, 5));
			p.add(new Point(4, 3));
			p.add(new Point(6, 4));
			Move m = new Move(players.get(0), p);
			
			return m;
		}
		
		if (rotationIndex == 3* players.size()) {
			ArrayList<Point> p = new ArrayList<Point>();
			p.add(new Point(2, 5));
			p.add(new Point(4, 6));
			Move m = new Move(players.get(0), p);
			
			return m;
		}
		else {
			while(true);
		}
	}*/
	
	private Move getMove() {
		Player currentPlayer = players.get(rotationIndex % players.size());
		scn = new Scanner(System.in);
		System.out.println("Please enter point you would like to move from as: row (from the top), index");
		int x = scn.nextInt();
		if (x == -2) {
			//test
			try {
				board.constructFinalBoard();
				return null;
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (x == -3) {
			//test
			ArrayList<Point> p = new ArrayList<Point>();
			p.add(new Point(0, 0));
			return new Move(getPlayer(rotationIndex % players.size()), p);
		}
		int y = scn.nextInt();
		Point from = new Point(x, y);
		Move m = new Move(currentPlayer, from);
		while (true) {
			System.out.println("Please inter point you would like to move to (only 1 jump) as: row (from the top), index" +
					"or -1 to end.");
			x = scn.nextInt();
			if (x == -1) {
				break;
			}
			y = scn.nextInt();
			Point to = new Point(x, y);
			m.add(to);
		}
		return m;
	}
	
	public void updateBoard(Move m) throws Exception {
		if (m == null) {
			return;
		}
		board.moveBall(m.getFrom(), m.getTo());
	}
	
	private boolean localPlayersTurn() {
		if (players.get(rotationIndex % players.size()).equals(localPlayer)) {
			return true;
		}
		return false;
	}
	
	//utility for constructor
	private ArrayList<Player> extractPlayers(List<Player> _players) throws Exception {
		if (_players.size() < 2 || _players.size() > 6) {
			throw new Exception("Invalid Number of Players");
		}
		
		ArrayList<Player> list = new ArrayList<Player>();
		for (Player p : _players) {
			list.add(p);
		}
		return list;
	}
	
	public int getNumPlayers() {
		return players.size();
	}

	public Player getPlayer(int i) {
		return players.get(i);
	}

	public Board getBoard() {
		return board;
	}
	
	public int getCurrentPlayerIndex() {
		return rotationIndex % players.size();
	}
}
