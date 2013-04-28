package hub;

public class HubMaintenance implements Runnable {

	MultiThreadedHub hub;
	
	public HubMaintenance( MultiThreadedHub hub ) {
		this.hub = hub;
	}
	
	@Override
	public void run() {
		while(true) {
			try {
				Thread.sleep(300000); // sleep for 5 minutes
			} catch (InterruptedException e) {
				continue;
			}
		}
	}

}
