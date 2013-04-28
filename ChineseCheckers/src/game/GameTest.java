package game;

import java.util.ArrayList;

public class GameTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		Player local = new Player("player0", 0);
		ArrayList<Player> l = new ArrayList<Player>();
		l.add(local);
		l.add(new Player("player1", 1));
		
		Interaction i = new TestInteraction();
		
		Game g = new Game(l, local, i);
	}

}
