package game;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.Key;
import java.security.PublicKey;
import java.util.List;
import java.util.Map;

import peer.Peer;
import utils.Constants;
import utils.EncryptUtils;
import utils.NetworkUtils;

public class GameListener implements Runnable {	
	
	private Key gameKey;
	private Map<String, PublicKey> verifyKeys;
	private List<String> players;
	
	private int rotation = 0;

	public GameListener(Key gameKey, Map<String, PublicKey> verifyKeys, List<String> players) {
		this.gameKey = gameKey;
		this.verifyKeys = verifyKeys;
		this.players = players;		
	}

	@Override
	public void run() {
		
		
	}
	
}
