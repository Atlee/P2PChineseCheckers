package game;

import java.awt.Point;
import java.util.ArrayList;

public class Rules {
	
	private static ArrayList<ArrayList<Point>> winningPoints;
	private static final int MAX_PLAYERS = 6;
	
	private static final Point[][] winningPos = { 
/*Player 0's winning Positions */	{ new Point(16, 6), new Point(15,5), new Point(15, 6), new Point(14, 5), new Point(14, 6), new Point(14, 7), new Point(13, 4), new Point(13, 5), new Point(13, 6), new Point(13, 7) },
/*Player 1's winning Positions */	{ new Point(0, 6),  new Point(1,5),  new Point(1, 6),  new Point(2, 5),  new Point(2, 6),  new Point(2, 7),  new Point(3, 4),  new Point(3, 5),  new Point(3, 6),  new Point(3, 7)  }
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
		return false;
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
		if (g.getNumPlayers() > winningPoints.size()) {
			throw new Exception ("Unknown winning conditions. " +
					g.getNumPlayers() + " Players and only " + winningPoints.size() + 
					" Conditions.");
		}
		
		int currentIndex = g.getCurrentPlayerIndex();
		Player p = g.getPlayer(currentIndex);
		boolean thisPlayerWins = true;
		for (Point pt : winningPoints.get(currentIndex)) {
			if (g.getBoard().containsBall(p, pt)) {
				thisPlayerWins = false;
			}
		}
		if (thisPlayerWins) {
			return true;
		}
		
		return false;
	}
}
