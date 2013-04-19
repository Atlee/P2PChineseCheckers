package game;

import java.awt.Point;
import java.util.ArrayList;

public class Rules {	
	private static final Point[][] winningPos = { 
/*Player 0's winning Positions */	{ new Point(6, 16), new Point(5, 15), new Point(6, 15), new Point(5, 14), new Point(6, 14), new Point(7, 14), new Point(4, 13), new Point(5, 13), new Point(6, 13), new Point(7, 13) },
/*Player 1's winning Positions */	{ new Point(6, 0),  new Point(5, 1),  new Point(6, 1),  new Point(5, 2),  new Point(6, 2),  new Point(7, 2),  new Point(4, 3),  new Point(5, 3),  new Point(6, 3),  new Point(7, 3)  }
	};
	
	public static boolean checkMove(Player p, Board b, Move m) {
		ArrayList<Point> jumps = m.getJumps();
		if (jumps.size() < 2) {
			//doesn't make sense for a jump to only have 1 point
			return false;
		}
		
		Point from = jumps.get(0);
		for (int i = 1; i < jumps.size(); i++) {
			Point to = jumps.get(i);
			if (!checkJump(p, b, from, to)) {
				//if any jump is invalid
				return false;
			}
			from = to;
		}
		return true;
	}
	
	public static boolean checkJump(Player p, Board b, Point from, Point to) {
		if(!b.onBoard(to)) {
			return false;
		}
		
		int direction = b.pointsAlign(from, to);
		
		if (direction == -1) {
			return false;
		}
		
		//check if a ball is in the middle hole
		if (!b.checkMiddle(from, direction)) {
			return false;
		}
		return true;
	}
	
	public static boolean gameOver(Game g) throws Exception{
		if (g.getNumPlayers() > winningPos.length) {
			throw new Exception ("Unknown winning conditions. " +
					g.getNumPlayers() + " Players and only " + winningPos.length + 
					" Conditions.");
		}
		
		int currentIndex = g.getCurrentPlayerIndex();
		Player p = g.getPlayer(currentIndex);
		boolean thisPlayerWins = true;
		//for every point the player needs to win, check if their ball fills that point
		//if not set this player wins to false
		for (Point pt : winningPos[currentIndex]) {
			if (!g.getBoard().containsBall(p, pt)) {
				thisPlayerWins = false;
				break;
			}
		}
		
		return thisPlayerWins;
	}
}
