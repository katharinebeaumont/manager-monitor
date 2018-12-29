package agent.monitor;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import agent.common.HeartbeatException;
import agent.common.HeartbeatRestController;
import agent.memory.domain.Application;
import agent.memory.domain.Location;

/**
 * Starts a scheduled thread from the monitoring agent to the application it is responsible for
 * and pings at a scheduled interval.
 * If the application agent is unavailable for a specified number of times (errorThreshold)
 * then the thread stops.
 */
@Service
public class MonitoringService {
	
	@Autowired
	private MonitoringRestController controller;
	
	@Autowired
	private ApplicationPidController pidController;
	
	@Autowired
	private HeartbeatRestController heartbeatController;
	
	private static final Logger log = LoggerFactory.getLogger(MonitoringService.class);
	
	@Value("${monitoring.metrics.endpoint}")
	private String metricsEndpoint;
	
	@Value("${monitoring.metrics.filter}")
	private String metricsFilter;

	@Value("${monitoring.interval}")
	private int interval;
	
	@Value("${monitoring.errorThreshold}")
	private int errorThreshold;
	
	@Value("${monitoring.initialDelay}")
	private int delay;

	private ScheduledExecutorService executorService;
	private int errorCounts = 0;
	
	public void startMonitoring(Application application) {

		String[] metricsFilterArray = metricsFilter.split(";");
		
		executorService = Executors.newScheduledThreadPool(1);
		
		class MonitoringTask implements Runnable {
			Application a;
			MonitoringTask(Application a) {this.a = a;}
			public void run() {
				
				Location location = a.getLocation();
				for (String filter: metricsFilterArray) {
					try {
						controller.monitor(a.getName(), location, metricsEndpoint + "/" + filter);
					} catch (Exception e){
						log.error("Error with monitoring " + a.getName() + " on port: "
								+ a.getLocation().getPort() + " for endpoint " + metricsEndpoint + "/" + filter);
						
						errorCounts++;
						
						if (errorCounts >= errorThreshold) {
							log.error("Stopping monitoring " + a.getName() + 
									" as error count (" + errorCounts + ") exceeds threshold of " + errorThreshold);
							stopMonitoring();
						}
					}
				}
			}
		}
		
	    final Runnable beater = new MonitoringTask(application);
	    TimeUnit timeUnit = TimeUnit.SECONDS;
	    executorService.scheduleAtFixedRate(beater, delay, interval, timeUnit);
	    log.info("Gathering metrics from " + application.getName() + " in " + delay + " " + timeUnit.toString());
	
	    getApplicationPid(application);
	}
	
	private void getApplicationPid(Application application) {
		final ScheduledExecutorService scheduler =
			     Executors.newScheduledThreadPool(1);
		
		class ApplicationPidTask implements Runnable {
			Application a;
			ApplicationPidTask(Application a) {this.a = a;}
			public void run() {
				
				Location location = a.getLocation();
				
					try {
						pidController.getAppPid(a);
						log.info("Got the pid for the application: " + a.getName());
					} catch (Exception e){
						log.error("Error with getting pid for " + a.getName() + " on port: "
								+ a.getLocation().getPort());
					}
			}
		}
		
		
	    final Runnable pidTask = new ApplicationPidTask(application);
	    scheduler.schedule(pidTask, delay, TimeUnit.SECONDS);
	}

	private void stopMonitoring() {
		ScheduledExecutorService scheduler = executorService;
		scheduler.shutdown();
		executorService = null;
		errorCounts = 0;
	}

	public void ping(Application application) throws HeartbeatException{
		heartbeatController.beat(application.getName(), application.getLocation());
	}
	
}
