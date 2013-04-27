package hub;

public class DeadGameReaper implements Runnable {

	MultiThreadedHub hub;
	
	public DeadGameReaper( MultiThreadedHub hub ) {
		this.hub = hub;
	}
	
	@Override
	public void run() {
		while(true) {
			try {
				Thread.sleep(300000); // sleep for 5 minutes
				hub.games.reapDeadGames();
			} catch (InterruptedException e) {
				continue;
			}
		}
	}

}
