package game;

import java.awt.Point;

public class Board {
	
	//create a placeholder Player to differentiate between a emtpy peg and a invalid peg
	public static final Player empty = new Player("empty", -1);
	
	//valid places in the 2d array for pegs to exist
	private static final boolean[][] board = { 
		{ false, false, false, false, false, false, true, false, false, false, false, false, false }, // 0

			{ false, false, false, false, false,  true, true, false, false, false, false, false, false }, // 1

		{ false, false, false, false, false,  true, true,  true, false, false, false, false, false }, // 2

			{ false, false, false, false,  true,  true, true,  true, false, false, false, false, false }, // 3

		{  true,  true,  true,  true,  true,  true, true,  true,  true,  true,  true,  true,  true }, // 4

			{  true,  true,  true,  true,  true,  true, true,  true,  true,  true,  true,  true, false }, // 5

		{ false,  true,  true,  true,  true,  true, true,  true,  true,  true,  true,  true, false }, // 6

			{ false,  true,  true,  true,  true,  true, true,  true,  true,  true,  true, false, false }, // 7

		{ false, false,  true,  true,  true,  true, true,  true,  true,  true,  true, false, false }, // 8 ----------------------

			{ false,  true,  true,  true,  true,  true, true,  true,  true,  true,  true, false, false }, // 7

		{ false,  true,  true,  true,  true,  true, true,  true,  true,  true,  true,  true, false }, // 6

			{  true,  true,  true,  true,  true,  true, true,  true,  true,  true,  true,  true, false }, // 5

		{  true,  true,  true,  true,  true,  true, true,  true,  true,  true,  true,  true,  true }, // 4

			{ false, false, false, false,  true,  true, true,  true, false, false, false, false, false }, // 3

		{ false, false, false, false, false,  true, true,  true, false, false, false, false, false }, // 2

			{ false, false, false, false, false,  true, true, false, false, false, false, false, false }, // 1

		{ false, false, false, false, false, false, true, false, false, false, false, false, false }, // 0
		};
	
	//valid places in the 2d array for any collor pegs to exist
	private static final boolean[][] middle = {
		{ false, false, false, false, false, false, false, false, false, false, false, false, false }, // 0

		{ false, false, false, false, false, false, false, false, false, false, false, false, false }, // 1

	{ false, false, false, false, false, false, false, false, false, false, false, false, false }, // 2

		{ false, false, false, false, false, false, false, false, false, false, false, false, false }, // 3

	{ false, false, false, false,  true,  true,  true,  true,  true, false, false, false, false }, // 4

		{ false, false, false,  true,  true,  true,  true,  true,  true, false, false, false, false }, // 5

	{ false, false, false,  true,  true,  true,  true,  true,  true,  true,  true, false, false }, // 6

		{ false, false,  true,  true,  true,  true,  true,  true,  true,  true,  true, false, false }, // 7

	{ false, false,  true,  true,  true,  true,  true,  true,  true,  true,  true, false, false }, // 8 ----------------------

		{ false, false,  true,  true,  true,  true,  true,  true,  true,  true,  true, false, false }, // 7

	{ false, false, false,  true,  true,  true,  true,  true,  true,  true,  true, false, false }, // 6

		{ false, false, false,  true,  true,  true,  true,  true,  true, false, false, false, false }, // 5

	{ false, false, false, false,  true,  true,  true,  true,  true, false, false, false, false }, // 4

		{ false, false, false, false, false, false, false, false, false, false, false, false, false }, // 3

	{ false, false, false, false, false, false, false, false, false, false, false, false, false }, // 2

		{ false, false, false, false, false, false, false, false, false, false, false, false, false }, // 1

	{ false, false, false, false, false, false, false, false, false, false, false, false, false }, // 0
	};
	
	
	private Player[][] state = new Player[17][13];
	
	private final Game game;	
	
	public Board(Game g) throws Exception {
		this.game = g;
		this.fillEdges();
		for (int i = 0; i < middle.length; i++) {
			for (int j = 0; j < middle[0].length; j++) {
				if (middle[i][j]) {
					state[i][j] = empty;
				}
			}
		}
	}
	
	private void fillEdges() throws Exception {
		if (game.getNumPlayers() < 2) {
			throw new Exception("Too few players");
		}
		//fill top with player 0
		setState(game.getPlayer(0), 0, 6);
		setState(game.getPlayer(0), 1, 5); setState(game.getPlayer(0), 1, 6);
		setState(game.getPlayer(0), 2, 5); setState(game.getPlayer(0), 2, 6); setState(game.getPlayer(0), 2, 7);
		setState(game.getPlayer(0), 3, 4); setState(game.getPlayer(0), 3, 5); setState(game.getPlayer(0), 3, 6); setState(game.getPlayer(0), 3, 7);
		
		//fill bot with player 1
		setState(game.getPlayer(1), 16, 6);
		setState(game.getPlayer(1), 15, 5); setState(game.getPlayer(1), 15, 6);
		setState(game.getPlayer(1), 14, 5); setState(game.getPlayer(1), 14, 6); setState(game.getPlayer(1), 14, 7);
		setState(game.getPlayer(1), 13, 4); setState(game.getPlayer(1), 13, 5); setState(game.getPlayer(1), 13, 6); setState(game.getPlayer(1), 3, 7);
		
		//TODO: others left as null
	}
	
	private boolean setState(Player p, int i, int j) {
		if (Board.board[i][j]) {
			this.state[i][j] = p;
			return true;
		}
		return false;
	}

	public boolean onBoard(Point to) {
		return board[to.y][to.x];
	}

	/*Directions returned as follows
	 * 
	 *		   0  1
	 *		 5	 *	2
	 *		   4  3
	 */
	public int pointsAlign(Point from, Point to) {
		if (from.y == to.y) { // they are on the same line
			if (from.x - 2 == to.x) {
				return 5;
			} else if (from.x + 2 == to.x) {
				return 2;
			}
		} else if (from.y + 2 == to.y) {
			if (from.x - 1 == to.x) {
				return 4;
			} else if (from.x + 1 == to.x) {
				return 3;
			}
		} else if (from.y - 2 == to.y) {
			if (from.x - 1 == to.x) {
				return 0;
			} else if (from.x + 1 == to.x) {
				return 1;
			}
		}
		//return -1 if points do not align
		return -1;
	}

	public boolean checkMiddle(Point from, int direction) {
		int newI, newJ;
		switch (direction) {
		case 0:
			newI = from.y + 2;
			newJ = from.x - 1;
			break;
		case 1:
			newI = from.y + 2;
			newJ = from.x + 1;
			break;
		case 2:
			newI = from.y;
			newJ = from.x + 1;
			break;
		case 3:
			newI = from.y - 2;
			newJ = from.x + 1;
			break;
		case 4:
			newI = from.y - 2;
			newJ = from.x - 1;
			break;
		case 5:
			newI = from.y;
			newJ = from.x - 1;
			break;
		default:
			//set to 00 b/c it will return false;
			newI = 0;
			newJ = 0;
		}
		return !(state[newI][newJ].equals(empty));
	}
	
	
	
	public boolean containsBall(Player p, Point pt) {
		return state[pt.y][pt.x].equals(p); 
	}

	public void moveBall(Point from, Point to) throws Exception {
		if (state[from.y][from.x] == null || state[from.y][from.x].equals(empty)
				|| !state[to.y][to.x].equals(empty)) {
			throw new Exception("Tried to move from an empty spot or tried to move to a full spot.");
		} else {
			state[to.y][to.x] = state[from.y][from.x];
			state[from.y][from.x] = empty;
		}
	}
}
