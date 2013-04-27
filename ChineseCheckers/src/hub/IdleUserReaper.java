package hub;

public class IdleUserReaper implements Runnable {

	MultiThreadedHub hub;
	
	public IdleUserReaper( MultiThreadedHub hub ) {
		this.hub = hub;
	}
	
	@Override
	public void run() {
		while(true) {
			try {
				Thread.sleep(300000); // sleep for 5 minutes
				hub.online.reapIdleUsers();
			} catch (InterruptedException e) {
				continue;
			}
		}
	}

}
