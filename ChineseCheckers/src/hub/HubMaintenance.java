package hub;

import java.util.Map;

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
				hub.online.reapIdleUsers();
				Map<String, Integer> online = hub.online.listOnlineUsers();
				
			} catch (InterruptedException e) {
				continue;
			}
		}
	}

}
