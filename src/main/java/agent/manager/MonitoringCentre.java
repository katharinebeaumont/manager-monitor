package agent.manager;

import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import agent.common.Heartbeat;
import agent.common.HeartbeatException;
import agent.common.HeartbeatRestController;
import agent.manager.learning.MonitorStatus;
import agent.manager.learning.QLearningControllerManager;
import agent.memory.DBInterface;
import agent.memory.domain.Location;
import agent.memory.domain.Monitor;

@Component
public class MonitoringCentre {
	
	private static final Logger log = LoggerFactory.getLogger(MonitoringCentre.class);
	
	@Autowired
	PidRequestService pidService;
	
	@Autowired
	private HeartbeatRestController controller;
	
	@Autowired
	private QLearningControllerManager qLearningController;
	
	@Autowired
	DBInterface dbInterface;
	
	@Value("${agent.heartbeat.errorThreshold}")
	private int errorThreshold;
	
	@Value("${agent.heartbeat.interval}")
	private int interval;
	
	@Value("${agent.heartbeat.initialDelay}")
	private int delay;
	
	//@Value("${agent.pause}")
	private int intervalsToPause = 20;
	
	
	//Keep array of monitors pinging.
	//Keep array of threads.
	Thread[] qLearningThreads = {};
	HashMap<Monitor, Object> tasks = new HashMap<>();
	
	final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(5);
	
	public void begin(Monitor m) {
		
		//Used to do beginHeartbeatAndGetPid(monitor)
		pidService.requestAndProcessPid(m); //TODO 26.1.20 is this blocking?
		
		//We want a separate process to pick up the learning
		final Runnable task = new MonitorTask(m);
		TimeUnit timeUnit = TimeUnit.SECONDS;
	    log.info("Scheduled task with delay: " + delay + " and interval: " + interval);
	    scheduler.scheduleAtFixedRate(task, delay, interval, timeUnit);
	    tasks.put(m, task);
	}
	
	public void end(Monitor m) { 
		MonitorTask task = (MonitorTask)tasks.get(m);
		task.stop();
	}

	class MonitorTask implements Runnable {
		Monitor s;
		MonitorTask(Monitor s) {this.s = s;}
		boolean interrupt = false;
		boolean pause = false;
		int errorCount = 0;
		int pauseCount = 0;
		public void stop() {
			log.error("Stopping monitoring");
			interrupt = true;
		}
		
		public void run() {
			 
			if (interrupt) {
				return;
			}
			if (pause) {
				pauseCount++;
				log.info("Monitoring paused:" + (intervalsToPause - pauseCount));
				if (intervalsToPause == pauseCount) {
					pauseCount = 0;
					pause = false;
					log.info("Resuming monitoring");
				}
				return;
			}
			
			//Update each time.
			s = dbInterface.updateLocationForMonitor(s);
			Location loc = s.getLocation();
			
			try {
				String response = controller.beat(s.getName(), loc);
				//TODO: this should constitute some kind of error really...
				if (!response.contains("eventStoreEmpty")) {
					MonitorStatus status = new MonitorStatus(s.getName(), response, loc);
					pause = qLearningController.process(status);
					errorCount = 0; //reset as back in communication
				} else {
					log.info("Event store empty, not processing:" + response);
				}
			} catch (HeartbeatException hbe){
				errorCount++;
				if (errorCount >= errorThreshold) {
					qLearningController.processMonitorDownEvent(s.getName(), loc);
					interrupt = true;
				}
			} catch (Throwable t) {
				log.error("Unforseen error:" + t.getMessage());
				t.printStackTrace();
			}
		}
	}
	
}