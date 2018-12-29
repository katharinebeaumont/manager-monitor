package agent.manager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import agent.common.HeartbeatException;
import agent.deployment.DeploymentService;
import agent.memory.ApplicationEntityService;
import agent.memory.LocationEntityService;
import agent.memory.MonitoringEntityService;
import agent.memory.domain.Application;
import agent.memory.domain.Location;
import agent.memory.domain.Monitor;

@Service
public class ManagerAgentActions {
	
	private static final Logger log = LoggerFactory.getLogger(ManagerAgentActions.class);
	
	@Autowired
	private HeartbeatService heartbeatService;
	
	@Autowired
	private DeploymentService deploymentService;
	
	@Autowired
	private ApplicationEntityService appService;
	
	@Autowired
	private LocationEntityService locationService;
	
	@Autowired
	private MonitoringEntityService monitoringAgentService;
	
	@Autowired
	private ApplicationNamingService applicationNamingService;
	
	@Autowired
	private PidRequestService pidService;
	
	public void deployMonitor(Monitor monitor) {
		deploymentService.startMonitor(monitor);	
	}
	
	public void startMonitorIfNotRunning(Monitor monitor) {
		//if the monitor is already running, do nothing:
		try {
			heartbeatService.ping(monitor);
		} catch (HeartbeatException hbe) {
			deploymentService.startMonitor(monitor);
		}
	}
	
	public Monitor getMonitorForApplication(String name) {
		return monitoringAgentService.findForApplication(name, 1);
	}

	/*
	 * This also gets the pid
	 */
	public void beginHeartbeatAndGetPid(Monitor monitor) {
		heartbeatService.startHeartbeat(monitor);
		pidService.requestAndProcessPid(monitor);
	}	

	/**
	 * Action: shutdown and redeploy
	 */
	public void shutdownAndRedeploy(String agentName, Location location) {
		log.info("Shutting down " + agentName + " and moving to new location: " + location.toString());
		
		//Load in entities		
		Monitor	monitor = monitoringAgentService.findByName(agentName);
		Application app = appService.findByMonitor(agentName);
		Location loc = locationService.findForMonitor(agentName);
		monitor.setResponsibility(app);
		monitor.setLocation(loc);
		
		//Kill them
		try {
			shutdownMonitorAndApplication(monitor);
		} catch (Exception e) {
			log.error("Exception with shutting down and redeploy");
			log.error(e.getMessage());
			return;
		}
		
		//Set the new location of the application
		app.setLocation(location);
		appService.save(app);
		
		//update the monitor location
		setNewMonitorLocation(monitor, location);
		
		deployMonitor(monitor);
		beginHeartbeatAndGetPid(monitor);
	}
	
	/*
	 * Takes the location of the application, adds 10 to the port.
	 * Saves new location,
	 * assigns to monitor
	 * Saves monitor.
	 * Assumes any port can be used where application is being deployed.
	 */
	private void setNewMonitorLocation(Monitor monitor, Location applicationLocation) {
		int port = applicationLocation.getPort() + 10;
		Location locationForMonitor = new Location(applicationLocation.getPath(), applicationLocation.getType(), port);
		locationService.save(locationForMonitor);
		monitor.setLocation(locationForMonitor);
		log.info("New location for " + monitor.getName() + " is " + locationForMonitor.toString());
		monitoringAgentService.save(monitor);
	}

	/*
	 * Called from ManagerAgent to kick things off
	 */
	public Monitor generateMonitorAndAssignLocation(Application e) throws NoAvailableLocationException {
		Monitor newMonitor = generateMonitor(e);
		setNewMonitorLocation(newMonitor, e.getLocation()); 
		return newMonitor;
	}
	
	/*
	 * Generates monitor.
	 * Sets it as responsible for application
	 * Saves it.
	 * Does not assign a location.
	 */
	private Monitor generateMonitor(Application application) throws NoAvailableLocationException {
		Monitor monitor = new Monitor(application.getName());
		monitor.setResponsibility(application);
		monitoringAgentService.save(monitor);
		return monitor;
	}

	private void shutdownMonitorAndApplication(Monitor monitor) {
		log.info("Stopping heartbeat with " + monitor.getName() + " as I'm about to kill it.");
		heartbeatService.stopHeartBeat(monitor);
		deploymentService.killMonitorAndApplication(monitor); 
		try {
			log.info("Checking monitor has been killed in 5 seconds ");
			Thread.sleep(5000);
			heartbeatService.ping(monitor);
			log.info("Still able to ping monitor, so sleeping and trying again in 5 seconds ");
			Thread.sleep(5000);
			heartbeatService.ping(monitor);
			log.error("Still able to ping monitor after 5 second sleep.");
		} catch (HeartbeatException hbe) {
			log.info("Killed " + monitor.getName());
		} catch (InterruptedException e) {
			log.error("Error when sleeping");
		}
	}
	
	/**
	 * Action: Duplicate and Deploy
	 * @param agentName
	 * @param location
	 */
	public void duplicatedAndDeploy(String agentName, Location location) {
		//Load in entities
		Application app = appService.findByMonitor(agentName);
		
		//Create new application that is exactly the same but with 
		// a duplicate number
		String newAppName = applicationNamingService.generateName(app.getName());
		
		Application newApplication = new Application(newAppName, app.getJarName());
		newApplication.setLocation(location);
		appService.save(newApplication);
		log.info("Created new application " + newAppName + " for deployment to " + location.toString());
		
		Monitor newMonitor;
		try {
			newMonitor = generateMonitorAndAssignLocation(newApplication);
		} catch (NoAvailableLocationException e) {
			log.error("Could not generate monitor for " + newAppName + " as no locations available.");
			return;
		}
		log.info("Created new monitor " + newMonitor.getName());
		log.info("Deploying new monitor " + newMonitor.getName());
		
		deployMonitor(newMonitor);
		beginHeartbeatAndGetPid(newMonitor);
	}

	/*
	 * Called to check on free locations
	 */
	public List<Location> getAvailableLocations() {
		Collection<Location> locs = locationService.findFreeLocation();
		List<Location> retval = new ArrayList<Location>();
		retval.addAll(locs);
		return retval;
	}

}
