package agent.common;

import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

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
	
	private static final Logger log = LoggerFactory.getLogger(HeartbeatService.class);
	
	@Value("${agent.heartbeat.errorThreshold}")
	private int errorThreshold;
	
	@Value("${agent.heartbeat.interval}")
	private int interval;
	
	private HashMap<Heartbeat, ScheduledExecutorService> heartbeats = new HashMap<>();
	private HashMap<Heartbeat, Integer> errorCounts = new HashMap<>();
	
	public void startHeartBeat(Heartbeat heartbeat) {

		final ScheduledExecutorService scheduler =
			     Executors.newScheduledThreadPool(1);
		
		heartbeats.put(heartbeat, scheduler);
		errorCounts.put(heartbeat, 0);
		
		class HeartBeatTask implements Runnable {
			Heartbeat s;
			HeartBeatTask(Heartbeat s) {this.s = s;}
			public void run() {
				Location loc = s.getLocation();
				try {
					controller.beat(s.getName(), loc);
				} catch (Exception e){
					log.error("Error with heartbeat for " + s.getName() + " on port: " + loc.getPort());
					
					int errorCount = errorCounts.get(heartbeat);
					errorCount++;
					errorCounts.put(s, errorCount);
					if (errorCount >= errorThreshold) {
						log.error("Stopping heartbeat with " + s.getName() + 
								" as error count (" + errorCount + ") exceeds threshold of " + errorThreshold);
						stopHeartBeat(s);
					}
				}
			}
		}
		
	    final Runnable beater = new HeartBeatTask(heartbeat);
	    scheduler.scheduleAtFixedRate(beater, 30, interval, TimeUnit.SECONDS);
	}
	
	private void stopHeartBeat(Heartbeat heartbeat) {
		ScheduledExecutorService scheduler = heartbeats.get(heartbeat);
		scheduler.shutdown();
		heartbeats.remove(heartbeat);
		errorCounts.remove(heartbeat);
	}
	
}
