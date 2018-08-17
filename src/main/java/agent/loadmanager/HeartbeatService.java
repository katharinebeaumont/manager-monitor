package agent.loadmanager;

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
	private HeartbeatController controller;
	
	private static final Logger log = LoggerFactory.getLogger(HeartbeatService.class);
	
	@Value("${agent.heartbeat.errorThreshold}")
	private int errorThreshold;
	
	@Value("${agent.heartbeat.interval}")
	private int interval;
	
	private HashMap<Monitor, ScheduledExecutorService> heartbeats = new HashMap<>();
	private HashMap<Monitor, Integer> errorCounts = new HashMap<>();
	
	public void startHeartBeat(Monitor monitor, Location loc) {

		final ScheduledExecutorService scheduler =
			     Executors.newScheduledThreadPool(1);
		
		heartbeats.put(monitor, scheduler);
		errorCounts.put(monitor, 0);
		
		class HeartBeatTask implements Runnable {
			Monitor s;
			Location l;
			HeartBeatTask(Monitor s, Location l) {this.s = s; this.l=l;}
			public void run() {
				try {
					controller.beat(s, l);
				} catch (Exception e){
					log.error("Error with heartbeat for monitor " + s.getName() + " on port: " + l.getPort());
					log.error(e.getMessage());
					
					int errorCount = errorCounts.get(monitor);
					errorCount++;
					errorCounts.put(monitor, errorCount);
					if (errorCount >= errorThreshold) {
						log.error("Stopping heartbeat with " + s.getName() + 
								" as error count (" + errorCount + ") exceeds threshold of " + errorThreshold);
						stopHeartBeat(s);
					}
				}
			}
		}
		
	    final Runnable beater = new HeartBeatTask(monitor, loc);
	    scheduler.scheduleAtFixedRate(beater, 30, interval, TimeUnit.SECONDS);
	}
	
	private void stopHeartBeat(Monitor monitor) {
		ScheduledExecutorService scheduler = heartbeats.get(monitor);
		scheduler.shutdown();
		heartbeats.remove(monitor);
		errorCounts.remove(monitor);
	}
	
}
