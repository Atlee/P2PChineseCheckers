package game;

import java.io.IOException;
import java.net.Socket;
import java.security.Key;

import utils.Constants;
import utils.NetworkUtils;

public class NetworkLayer implements Interaction {

	Socket opponent;
	AuditLog log;
	Key key;
	
	public NetworkLayer(Socket s, Key gameKey) {
		opponent = s;
		log = new AuditLog();
		key = gameKey;
	}
	
	@Override
	public Move waitForOpponent() throws Exception {
		System.out.println("Waiting for opponent move.");
		String moveSerialized = new String(NetworkUtils.readEncryptedMessage(opponent, key, Constants.SHARED_ENCRYPT_ALG));
		return Move.deSerialize(moveSerialized);
	}

	@Override
	public void shareMove(Move m) throws IOException {
		String moveString = m.serialize();
		NetworkUtils.sendEncryptedMessage(opponent, moveString.getBytes(), key, Constants.SHARED_ENCRYPT_ALG);
		log.append(moveString);
	}

	@Override
	public void endGame(Player winner) {
		// add protocol for winner game end to hub
		log.append("winner:" + winner.getIndex() + ":" + winner.getUsername());
		//send log to hub
	}
}
