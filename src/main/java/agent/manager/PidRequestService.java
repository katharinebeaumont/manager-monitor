package agent.manager;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import agent.common.Heartbeat;
import agent.common.HeartbeatException;
import agent.common.PidRestController;
import agent.memory.domain.Location;

@Service
public class PidRequestService {

	private static final Logger log = LoggerFactory.getLogger(PidRequestService.class);
	
	@Value("${agent.heartbeat.initialDelay}")
	private int delay;
	
	@Autowired
	private PidRestController pidController;
	
	@Autowired
	private PidProcessingService processService;
	
	public void requestAndProcessPid(Heartbeat heartbeat) {
		//Waits a certain amount of time before requesting the PID
		final ScheduledExecutorService scheduler =
			     Executors.newScheduledThreadPool(1);
		
		class RequestPidTask implements Runnable {
			Heartbeat s;
			RequestPidTask(Heartbeat s) {this.s = s;}
			public void run() {
				
				Location loc = s.getLocation();
				try {
					String pid = pidController.requestPid(heartbeat.getName(), heartbeat.getLocation());
					processService.processPid(heartbeat, pid);
				} catch (HeartbeatException hbe){
					log.error("Error with PID for " + s.getName() + " on port: " + loc.getPort());
				}
			}
		}
		
		final Runnable pidTask = new RequestPidTask(heartbeat);
	    TimeUnit timeUnit = TimeUnit.SECONDS;
	    scheduler.schedule(pidTask, delay-5, timeUnit);
	}
	

}
