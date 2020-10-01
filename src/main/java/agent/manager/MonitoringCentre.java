package agent.manager;

import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import agent.common.Heartbeat;
import agent.common.HeartbeatException;
import agent.common.HeartbeatRestController;
import agent.manager.learning.MonitorStatus;
import agent.manager.learning.QLearningControllerManager;
import agent.memory.domain.Location;
import agent.memory.domain.Monitor;

@Component
public class MonitoringCentre {
	
	@Autowired
	PidRequestService pidService;
	
	@Autowired
	private HeartbeatRestController controller;
	
	@Autowired
	private QLearningControllerManager qLearningController;
	
	
	@Value("${agent.heartbeat.errorThreshold}")
	private int errorThreshold;
	
	@Value("${agent.heartbeat.interval}")
	private int interval;
	
	@Value("${agent.heartbeat.initialDelay}")
	private int delay;
	
	
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
	    scheduler.scheduleAtFixedRate(task, delay, interval, timeUnit);
	    tasks.put(m, task);
	}
	
	public void end(Monitor m) { 
		MonitorTask task = (MonitorTask)tasks.get(m);
		task.stop();
	}

	class MonitorTask implements Runnable {
		Heartbeat s;
		MonitorTask(Heartbeat s) {this.s = s;}
		boolean interrupt = false;
		int errorCount = 0;
		
		public void stop() {
			interrupt = true;
		}
		
		public void run() {
			 
			if (interrupt) {
				return;
			}
			
			Location loc = s.getLocation();
			try {
				String response = controller.beat(s.getName(), loc);
				MonitorStatus status = new MonitorStatus(s.getName(), response);
				qLearningController.process(status);
			} catch (HeartbeatException hbe){
				errorCount++;
				if (errorCount >= errorThreshold) {
					qLearningController.processMonitorDownEvent(s.getName());
					interrupt = true;
				}
			}
		}
	}
	
}