package game;

import java.io.IOException;

public class TestInteraction implements Interaction {

	@Override
	public Move waitForOpponent(String pname) throws IOException, Exception {
		// TODO Auto-generated method stub
		System.out.println(pname);
		return null;
	}

	@Override
	public void shareMove(Move m) throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public void endGame(Player host, Player player) {
		// TODO Auto-generated method stub
		
	}

}
