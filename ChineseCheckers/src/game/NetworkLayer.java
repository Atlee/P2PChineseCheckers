package game;

import java.io.IOException;
import java.net.Socket;
import java.security.Key;
import java.security.KeyPair;
import java.security.PublicKey;
import java.util.Map;

import utils.Constants;
import utils.EncryptUtils;
import utils.NetworkUtils;
import utils.SignUtils;

public class NetworkLayer implements Interaction {

	Socket opponent;
	Socket hub;
	AuditLog log;
	Key gameKey;
	KeyPair signKeys;
	Map<String, PublicKey> verifyKeys;
	
	public NetworkLayer(Socket s, Key gameKey) {
		opponent = s;
		log = new AuditLog();
		this.gameKey = gameKey;
		signKeys = null;
	}
	
	public NetworkLayer(Socket s, Key gameKey, KeyPair localSignKeys, Map<String, PublicKey> verifyKeys) {
		opponent = s;
		log = new AuditLog();
		this.gameKey = gameKey;
		this.signKeys = signKeys;
		this.verifyKeys = verifyKeys;
	}
	
	@Override
	public Move waitForOpponent() throws Exception {
		System.out.println("Waiting for opponent move.");
		String moveSerialized = new String(NetworkUtils.readEncryptedMessage(opponent, gameKey, Constants.SHARED_ENCRYPT_ALG));
		log.append(moveSerialized);
		return Move.deSerialize(moveSerialized);
	}

	@Override
	public void shareMove(Move m) throws IOException {
		String moveString = m.serialize();
		NetworkUtils.sendEncryptedMessage(opponent, moveString.getBytes(), gameKey, Constants.SHARED_ENCRYPT_ALG);
		log.append(moveString);
	}

	@Override
	public void endGame(Player host, Player winner) {
		// add protocol for winner game end to hub
		Socket hub = NetworkUtils.handleCreateSocket();
		Key hubKey = EncryptUtils.handleCreateSharedKey();
		
		log.prepend("winner:"+ winner.getUsername());
		
    	try {
    		NetworkUtils.sendEncryptedMessage(hub, hubKey.getEncoded(), Constants.getHubPublicKey(), Constants.PUBLIC_ENCRYPT_ALG);
    		NetworkUtils.sendProtocolID(hub, Constants.GET_LOG);
    		
    		NetworkUtils.sendEncryptedMessage(hub, host.getUsername().getBytes(), hubKey, Constants.SHARED_ENCRYPT_ALG);
        	
        	NetworkUtils.sendEncryptedMessage(hub, log.getBytes(), hubKey, Constants.SHARED_ENCRYPT_ALG);
    	} catch (IOException e) {
    		e.printStackTrace();
    		System.exit(1);
    	}
    	
		//send log to hub
	}
	
	public void sendSignEncryptMessage(Socket s, byte[] message, Key encryptKey, String encryptAlg) throws IOException {
		byte[] signedData = SignUtils.signData(signKey, message);
		NetworkUtils.sendEncryptedMessage(s, signedData, encryptKey, encryptAlg);
	}
}
