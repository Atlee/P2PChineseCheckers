package peer;

import java.io.IOException;
import java.net.Socket;
import java.security.Key;
import java.security.PrivateKey;
import java.security.PublicKey;

import utils.Constants;
import utils.EncryptUtils;
import utils.NetworkUtils;
import utils.SignUtils;

import game.AuditLog;
import game.Interaction;
import game.Move;
import game.Player;

public class TwoPlayerLayer implements Interaction {
	
	private Socket s;
	private Key encryptKey;
	private PrivateKey signKey;
	private PublicKey verifyKey;
	private AuditLog log = new AuditLog();

	TwoPlayerLayer(Socket s, Key encryptKey, PrivateKey signKey, PublicKey verifyKey) {
		this.s = s;
		this.encryptKey = encryptKey;
		this.signKey = signKey;
		this.verifyKey = verifyKey;
	}

	@Override
	public Move waitForOpponent(String pname) throws IOException, Exception {
		System.out.println("Waiting for opponent move.");
		String playerName = new String(NetworkUtils.readEncryptedMessage(s, encryptKey, Constants.SHARED_ENCRYPT_ALG));
		System.out.println(playerName);
		System.out.println(pname);
		if (pname.equals(playerName)) {
			//byte[] ciphertext = NetworkUtils.readSignedMessage(s, verifyKey);
			//printFirst(ciphertext);
			String moveSerialized = new String(NetworkUtils.readEncryptedMessage(s, encryptKey, Constants.SHARED_ENCRYPT_ALG));
				System.out.println(moveSerialized);
				log.append(moveSerialized);
				return Move.deSerialize(moveSerialized);
		} else {
			return null;
		}
	}
	
	private void printFirst(byte[] arr) {
		for (int i = 0; i < arr.length && i < 5; i++) {
			System.out.println(arr[i]);
		}
	}

	@Override
	public void shareMove(Move m) throws IOException {
		String moveString = m.serialize();
		NetworkUtils.sendEncryptedMessage(s, m.getPlayer().getBytes(), encryptKey, Constants.SHARED_ENCRYPT_ALG);
		NetworkUtils.sendEncryptedMessage(s, moveString.getBytes(), encryptKey, Constants.SHARED_ENCRYPT_ALG);
		//sendSignEncryptMessage(s, moveString.getBytes(), encryptKey, Constants.SHARED_ENCRYPT_ALG);
		log.append(moveString);

	}

	@Override
	public void endGame(Player host, Player winner) {
		// TODO Auto-generated method stub

	}
	
	public void sendSignEncryptMessage(Socket s, byte[] message, Key encryptKey, String encryptAlg) throws IOException {
		byte[] ciphertext = EncryptUtils.encryptData(message, encryptKey, Constants.SHARED_ENCRYPT_ALG);
		printFirst(ciphertext);
		byte[] signedData = SignUtils.signData(signKey, ciphertext);
		NetworkUtils.sendSignedMessage(s, ciphertext, signKey);
	}

}
