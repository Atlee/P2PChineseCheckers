package game;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public class Game {
	//order of players in these lists are important
	//These lists should be coming from the hub, therefore everyone will
	//receive the same order list
	private final ArrayList<Player> players;
	private Board board;
	private int rotationIndex = 0;
	private Player localPlayer;
	private Interaction communication;
	
	public Game(List<Player> _players, Player _localPlayer, Interaction _communication) throws Exception {
		players = extractPlayers(_players);
		board = new Board(this);
		localPlayer = _localPlayer;
		communication = _communication;
	}

	public void start() throws Exception {
		while (!Rules.gameOver(this)) {
			board.printBoard();
			Move m;
			if (this.localPlayersTurn()) {
				m = getMove();
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
	}
	
	private Move getMove() {
		Player currentPlayer = players.get(rotationIndex % players.size());
		Scanner scn = new Scanner(System.in);
		System.out.println("Please enter point you would like to move from as: row (from the top), index");
		int x = scn.nextInt();
		if (x == -2) {
			//TODO:Remove this
			
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
	
	private HashMap<String, Integer> getPlayerIndex() {
		HashMap<String, Integer> output = new HashMap<String, Integer>();
		for (int i = 0; i < players.size(); i++) {
			output.put(players.get(i).getUsername(), i);
		}
		return output;
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
