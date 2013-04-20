package game;

import java.io.IOException;
import java.net.Socket;

import utils.NetworkUtils;

public class NetworkLayer implements Interaction {

	Socket opponent;
	AuditLog log;
	
	public NetworkLayer(Socket s) {
		opponent = s;
		log = new AuditLog();
	}
	
	@Override
	public Move waitForOpponent() throws Exception {
		System.out.println("Waiting for opponent move.");
		String moveSerialized = new String(NetworkUtils.readMessage(opponent));
		return Move.deSerialize(moveSerialized);
	}

	@Override
	public void shareMove(Move m) throws IOException {
		String moveString = m.serialize();
		NetworkUtils.sendMessage(opponent, moveString.getBytes());
		log.append(moveString);
	}

	@Override
	public void endGame(Player winner) {
		// add protocol for winner game end to hub
		log.append("winner:" + winner.getIndex() + ":" + winner.getUsername());
		//send log to hub
	}

}
