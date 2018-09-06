package agent.manager;

import java.util.HashMap;
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
import agent.common.HeartbeatRestController;
import agent.manager.learning.QLearningController;
import agent.memory.domain.Application;
import agent.memory.domain.Location;
import agent.memory.domain.Monitor;

/**
 * Starts a scheduled thread for each monitoring agent and pings at a scheduled interval.
 * If the monitoring agent is unavailable for a specified number of times (errorThreshold)
 * then the heartbeat stops.
 */
@Service
public class HeartbeatService {

	@Autowired
	private HeartbeatRestController controller;
	
	@Autowired
	private QLearningController qLearningController;
	
	private static final Logger log = LoggerFactory.getLogger(HeartbeatService.class);
	
	@Value("${agent.heartbeat.errorThreshold}")
	private int errorThreshold;
	
	@Value("${agent.heartbeat.interval}")
	private int interval;
	
	@Value("${agent.heartbeat.initialDelay}")
	private int delay;
	
	private HashMap<String, ScheduledExecutorService> heartbeats = new HashMap<>();
	private HashMap<String, Integer> errorCounts = new HashMap<>();
	
	public void ping(Heartbeat heartbeat) throws HeartbeatException {
		controller.beat(heartbeat.getName(), heartbeat.getLocation());
	}
	
	public void startHeartBeat(Heartbeat heartbeat) {

		final ScheduledExecutorService scheduler =
			     Executors.newScheduledThreadPool(1);
		
		heartbeats.put(heartbeat.getName(), scheduler);
		errorCounts.put(heartbeat.getName(), 0);
		
		class HeartBeatTask implements Runnable {
			Heartbeat s;
			HeartBeatTask(Heartbeat s) {this.s = s;}
			public void run() {
				
				Location loc = s.getLocation();
				try {
					String response = controller.beat(s.getName(), loc);
					qLearningController.process(s.getName(), loc, response);
				} catch (HeartbeatException hbe){
					log.error("Error with heartbeat for " + s.getName() + " on port: " + loc.getPort());
					
					int errorCount = errorCounts.get(heartbeat.getName());
					errorCount++;
					errorCounts.put(s.getName(), errorCount);
					if (errorCount >= errorThreshold) {
						qLearningController.processMonitorDownEvent(s.getName(), loc);
					}
				}
			}
		}
		
	    final Runnable beater = new HeartBeatTask(heartbeat);
	    TimeUnit timeUnit = TimeUnit.SECONDS;
	    scheduler.scheduleAtFixedRate(beater, delay, interval, timeUnit);
	    log.info("Starting heartbeat with " + heartbeat.getName() + " in " + delay + " " + timeUnit.toString());
		
	}
	
	public void stopHeartBeat(Heartbeat heartbeat) {
		ScheduledExecutorService scheduler = heartbeats.get(heartbeat.getName());
		if (scheduler != null) {
			scheduler.shutdown();
			heartbeats.remove(heartbeat.getName());
			errorCounts.remove(heartbeat.getName());
		}
	}
}
