package game;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public class Move {
	
	private ArrayList<Point> moves;
	private final Player player;
	
	public Move(Player player, Point p) {
		moves = new ArrayList<Point>();
		
		this.moves.add(p);
		this.player = player;
	}
	
	public Move(Player player, List<Point> moveList) {
		moves = new ArrayList<Point>();
		this.player = player; 
		
		for (Point p : moveList) {
			moves.add(p);
		}
	}
	
	public String serialize() {
		String output = player.getIndex() + ":" + player.getUsername() + ":";
		for (Point p : moves) {
			output = output + p.x + "," + p.y + ";";
		}
		return output;
	}
	
	public static Move deSerialize(String m) throws Exception {
		String[] strings = m.split(":");
		if (strings.length < 3) {
			throw new Exception("deserialization failed: " + m);
		}
		Player p = new Player(strings[1], Integer.parseInt(strings[0]));
		
		ArrayList<Point> points = new ArrayList<Point>();
		
		String[] stringPoints = strings[2].split(";");
		for (int i = 0; i < stringPoints.length; i++) {
			String[] XandY = stringPoints[i].split(",");
			if (XandY.length < 2) {
				throw new Exception("deserialization 2 failed: " + XandY);
			}
			
			points.add(new Point(Integer.parseInt(XandY[0]), Integer.parseInt(XandY[1])));
		}
		
		return new Move(p, points);
	}
	
	public void add(Point p) {
		moves.add(p);
	}
	
	public void add(int i, int j) {
		this.add(new Point(i, j));
	}
	
	public Point getFrom() {
		if (!moves.isEmpty()) {
			return moves.get(0);
		}
		return null;
	}
	
	public Point getTo() {
		if (moves.size() < 2) {
			return null;
		} else {
			return moves.get(moves.size() - 1);
		}
	}
	
	public ArrayList<Point> getJumps() {
		return moves;
	}
}
