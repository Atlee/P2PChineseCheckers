package game;

import java.io.IOException;
import java.net.Socket;
import java.security.Key;
import java.security.PrivateKey;
import java.security.PublicKey;

import utils.Constants;
import utils.NetworkUtils;
import utils.SignUtils;

public class NetworkLayer implements Interaction {

	Socket opponent;
	AuditLog log;
	Key encryptKey;
	PublicKey verifyKey;
	PrivateKey signKey;
	
	public NetworkLayer(Socket s, Key gameKey) {
		opponent = s;
		log = new AuditLog();
		encryptKey = gameKey;
		verifyKey = null;
		signKey = null;
	}
	
	public NetworkLayer(Socket s, Key gameKey, PrivateKey signKey, PublicKey verifyKey) {
		opponent = s;
		log = new AuditLog();
		encryptKey = gameKey;
		this.verifyKey = verifyKey;
		this.signKey = signKey;
	}
	
	@Override
	public Move waitForOpponent() throws Exception {
		System.out.println("Waiting for opponent move.");
		String moveSerialized = new String(NetworkUtils.readEncryptedMessage(opponent, encryptKey, Constants.SHARED_ENCRYPT_ALG));
		log.append(moveSerialized);
		return Move.deSerialize(moveSerialized);
	}

	@Override
	public void shareMove(Move m) throws IOException {
		String moveString = m.serialize();
		NetworkUtils.sendEncryptedMessage(opponent, moveString.getBytes(), encryptKey, Constants.SHARED_ENCRYPT_ALG);
		log.append(moveString);
	}

	@Override
	public void endGame(Player winner) {
		// add protocol for winner game end to hub
		log.append("winner:" + winner.getIndex() + ":" + winner.getUsername());
		//send log to hub
	}
	
	public void sendSignEncryptMessage(Socket s, byte[] message, Key encryptKey, String encryptAlg) throws IOException {
		byte[] signedData = SignUtils.signData(signKey, message);
		NetworkUtils.sendEncryptedMessage(s, signedData, encryptKey, encryptAlg);
	}
}
