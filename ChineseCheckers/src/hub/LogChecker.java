package hub;


public class LogChecker implements Runnable {
	
	MultiThreadedHub hub;
	GameRecord gameRecord;

	public LogChecker( MultiThreadedHub hub, GameRecord gameRecord ) {
		this.hub = hub;
		this.gameRecord = gameRecord;
	}
	
	@Override
	public void run() {
		
	}

}
