package game;

import java.io.IOException;

/**
 * An interface for the game to perform the functions it needs to perform to interact with
 * the other players without requiring any communication specific code
 * 
 * @author Atlee
 *
 */

public interface Interaction {

	public Move waitForOpponent(String pname) throws IOException, Exception;
	
	public void shareMove(Move m) throws IOException;

	public void endGame(Player host, Player winner);
}
