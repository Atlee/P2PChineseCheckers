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

	{ false, false, false,  true,  true,  true,  true,  true,  true,  true,  false, false, false }, // 6

		{ false, false,  true,  true,  true,  true,  true,  true,  true,  true,  false, false, false }, // 7

	{ false, false,  true,  true,  true,  true,  true,  true,  true,  true,  true, false, false }, // 8 ----------------------

		{ false, false,  true,  true,  true,  true,  true,  true,  true,  true,  false, false, false }, // 7

	{ false, false, false,  true,  true,  true,  true,  true,  true,  true,  false, false, false }, // 6

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
		setState(game.getPlayer(1), 13, 4); setState(game.getPlayer(1), 13, 5); setState(game.getPlayer(1), 13, 6); setState(game.getPlayer(1), 13, 7);
		
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
		return board[to.x][to.y];
	}

	/*Directions returned as follows
	 * 
	 *		   0  1
	 *		 5	 *	2
	 *		   4  3
	 */
	public int pointsAlignLong(Point from, Point to) {
		if (from.x == to.x) { // they are on the same line
			if (from.y - 2 == to.y) { 
				return 5;
			} else if (from.y + 2 == to.y) {
				return 2;
			}
		} else if (from.x + 2 == to.x) {
			if (from.y - 1 == to.y) {
				return 4;
			} else if (from.y + 1 == to.y) {
				return 3;
			}
		} else if (from.x - 2 == to.x) {
			if (from.y - 1 == to.y) {
				return 0;
			} else if (from.y + 1 == to.y) {
				return 1;
			}
		}
		//return -1 if points do not align
		return -1;
	}
	
	public int pointsAlignShort(Point from, Point to) {
		if  (from.x == to.x) {
			if (from.y + 1 == to.y) {
				return 2;
			} else if (from.y - 1 == to.y) {
				return 5;
			}
		} else if (from.x + 1 == to.x) {
			if (from.y == to.y) {
				if (from.x % 2 == 0) {//if were on an even line and the j coor doesn't differ
					return 3;
				} else {
					return 4;
				}				
			} else if (from.y - 1 == to.y && from.x % 2 == 0) {
				return 4;
			} else if (from.y + 1 == to.y && from.x % 2 == 1) {
				return 3;
			}
		} else if (from.x - 1 == to.x) {
			if (from.y == to.y) {
				if (from.x % 2 == 0) {//if were on an even line and the j coor doesn't differ
					return 1;
				} else {
					return 0;
				}				
			} else if (from.y - 1 == to.y && from.x % 2 == 0) {
				return 0;
			} else if (from.y + 1 == to.y && from.x % 2 == 1) {
				return 1;
			}
		}
		return -1;
	}

	public boolean checkMiddle(Point from, int direction) {
		int newI, newJ;
		switch (direction) {
		case 0:
			newI = from.x - 2;
			newJ = from.y - 1;
			break;
		case 1:
			newI = from.x - 2;
			newJ = from.y + 1;
			break;
		case 2:
			newI = from.x;
			newJ = from.y + 1;
			break;
		case 3:
			newI = from.x + 2;
			newJ = from.y + 1;
			break;
		case 4:
			newI = from.x + 2;
			newJ = from.y - 1;
			break;
		case 5:
			newI = from.x;
			newJ = from.y - 1;
			break;
		default:
			//set to 00 b/c it will return false;
			newI = 0;
			newJ = 0;
		}
		return !(state[newI][newJ].equals(empty));
	}
	
	
	
	public boolean containsBall(Player p, Point pt) {
		if (state[pt.y][pt.x] == null) {
			return false;
		}
		return state[pt.y][pt.x].equals(p); 
	}

	public void moveBall(Point from, Point to) throws Exception {
		if (state[from.x][from.y] == null || state[from.x][from.y].equals(empty)
				|| !state[to.x][to.y].equals(empty)) {
			throw new Exception("Tried to move from an empty spot or tried to move to a full spot.");
		} else {
			state[to.x][to.y] = state[from.x][from.y];
			state[from.x][from.y] = empty;
		}
	}
	
	public void printBoard() {
		printIndex();
		for (int i = 0; i < board.length; i++) {
			if (i % 2 == 1) {
				System.out.print("  ");
			}
			for (int j = 0; j < board[0].length; j++) {
				if (board[i][j]) {
					if (state[i][j] != null) {
						if (state[i][j].getIndex() == -1) {
							//System.out.print(state[i][j].getIndex());
							System.out.print("__");
						} else {
							System.out.print(" "+state[i][j].getIndex());
						}
					} else {
						System.out.print("__");
					}
				} else {
					System.out.print("  ");
				}
				System.out.print("  ");
			}
			System.out.println();
		}
	}
	
	public void printIndex() {
		int numPlayers = game.getNumPlayers();
		
		System.out.println("-----------------------------------------------------------------------------------");
		for (int i = 0; i < numPlayers; i++) {
			System.out.println("|\t" + i + ": " + game.getPlayer(i).getUsername() + "\t\t|");
		}
		System.out.println("-----------------------------------------------------------------------------------");
	}

	public void constructFinalBoard() throws Exception {
		if (game.getNumPlayers() < 2) {
			throw new Exception("Too few players");
		}
		//fill top with player 0
		setState(game.getPlayer(1), 0, 6);
		setState(game.getPlayer(1), 1, 5); setState(game.getPlayer(1), 1, 6);
		setState(game.getPlayer(1), 2, 5); setState(game.getPlayer(1), 2, 6); setState(game.getPlayer(1), 2, 7);
		setState(game.getPlayer(1), 3, 4); setState(game.getPlayer(1), 3, 5); setState(game.getPlayer(1), 3, 6); setState(game.getPlayer(1), 3, 7);
		
		//fill bot with player 1
		setState(game.getPlayer(0), 16, 6);
		setState(game.getPlayer(0), 15, 5); setState(game.getPlayer(0), 15, 6);
		setState(game.getPlayer(0), 14, 5); setState(game.getPlayer(0), 14, 6); setState(game.getPlayer(0), 14, 7);
		setState(game.getPlayer(0),  9, 4); setState(game.getPlayer(0), 13, 5); setState(game.getPlayer(0), 13, 6); setState(game.getPlayer(0), 13, 7);
		
		//TODO: others left as null
	}

	//return true if the jump is more than a single move else return false
	public boolean longJump(Point from, Point to) {
		if (from.x == to.x) {
			return Math.abs(from.y - to.y) >= 2;
		} else {
			return (Math.abs(from.y - to.y) + Math.abs(from.x - to.x)) > 2;
		}
	}

	public boolean isPlayer(Player p, Point from) {
		return state[from.x][from.y].equals(p);
	}
}
