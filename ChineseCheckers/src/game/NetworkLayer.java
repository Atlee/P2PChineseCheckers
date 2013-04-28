package game;

import java.io.IOException;
import java.net.Socket;
import java.security.Key;
import java.security.KeyPair;
import java.security.PublicKey;
import java.util.List;
import java.util.Map;

import utils.Constants;
import utils.EncryptUtils;
import utils.NetworkUtils;
import utils.SignUtils;

public class NetworkLayer implements Interaction {

	Map<String, Socket> opponents;
	Socket hub;
	AuditLog log;
	Key gameKey;
	KeyPair signKeys;
	Map<String, PublicKey> verifyKeys;
	
	/*public NetworkLayer(Socket s, Key gameKey) {
		opponent = s;
		log = new AuditLog();
		this.gameKey = gameKey;
		signKeys = null;
	}*/
	
	public NetworkLayer(Key gameKey, KeyPair localSignKeys, Map<String, PublicKey> verifyKeys, List<String> players) {
		opponents = null; // FIXME
		log = new AuditLog();
		this.gameKey = gameKey;
		this.signKeys = localSignKeys;
		this.verifyKeys = verifyKeys;
	}
	
	@Override
	public Move waitForOpponent(String pname) throws Exception {
		System.out.println("Waiting for opponent move.");
		String playerName = new String(NetworkUtils.readEncryptedMessage(opponents.get(pname), gameKey, Constants.SHARED_ENCRYPT_ALG));
		if (pname == playerName) {
			byte[] ciphertext = NetworkUtils.readSignedMessage(opponents.get(pname), verifyKeys.get(playerName));
			if (ciphertext != null) {
				String moveSerialized = new String(EncryptUtils.decryptData(ciphertext, gameKey, Constants.SHARED_ENCRYPT_ALG));
				log.append(moveSerialized);
				return Move.deSerialize(moveSerialized);
			} else {
				System.out.println("CHEATER");
				return null;
			}
		} else {
			return null;
		}
	}

	@Override
	public void shareMove(Move m) throws IOException {
		String moveString = m.serialize();
			for (String pname : opponents.keySet()) {
			NetworkUtils.sendEncryptedMessage(opponents.get(pname), m.getPlayer().getBytes(), gameKey, Constants.SHARED_ENCRYPT_ALG);
			sendSignEncryptMessage(opponents.get(pname), moveString.getBytes(), gameKey, Constants.SHARED_ENCRYPT_ALG);
		}
		log.append(moveString);
	}

	@Override
	public void endGame(Player host, Player winner) {
		// add protocol for winner game end to hub
		/*Socket hub = NetworkUtils.handleCreateSocket();
		Key hubKey = EncryptUtils.handleCreateSharedKey();
		
		log.prepend("winner:"+ winner.getUsername());
		
    	try {
    		NetworkUtils.sendEncryptedMessage(hub, hubKey.getEncoded(), Constants.getHubPublicKey(), Constants.PUBLIC_ENCRYPT_ALG);
    		NetworkUtils.sendProtocolID(hub, Constants.LOG);
    		
    		NetworkUtils.sendEncryptedMessage(hub, host.getUsername().getBytes(), hubKey, Constants.SHARED_ENCRYPT_ALG);
        	
        	NetworkUtils.sendEncryptedMessage(hub, log.getBytes(), hubKey, Constants.SHARED_ENCRYPT_ALG);
    	} catch (IOException e) {
    		e.printStackTrace();
    		System.exit(1);
    	}*/
    	
		//send log to hub
	}
	
	public void sendSignEncryptMessage(Socket s, byte[] message, Key encryptKey, String encryptAlg) throws IOException {
		byte[] ciphertext = EncryptUtils.encryptData(message, encryptKey, Constants.SHARED_ENCRYPT_ALG);
		byte[] signedData = SignUtils.signData(signKeys.getPrivate(), ciphertext);
		NetworkUtils.sendMessage(s, signedData);
	}
}
