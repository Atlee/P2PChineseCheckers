package game;

import java.io.IOException;
import java.net.Socket;

import utils.NetworkUtils;

public class NetworkLayer implements Interaction {

	Socket opponent;
	
	public NetworkLayer(Socket s) {
		opponent = s;
	}
	
	@Override
	public Move waitForOpponent() throws Exception {
		String moveSerialized = new String(NetworkUtils.readMessage(opponent));
		return Move.deSerialize(moveSerialized);
	}

	@Override
	public void shareMove(Move m) throws IOException {
		NetworkUtils.sendMessage(opponent, m.serialize().getBytes());
	}

	@Override
	public void endGame(Player winner) {
		// add protocol for winner game end to hub
		
	}

}
