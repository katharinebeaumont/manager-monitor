package agent.manager;

import java.util.Collection;

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
	private LocationDecisionService locationDecision;
	
	@Autowired
	private MonitoringEntityService monitoringAgentService;
	
	//TODO: eventually this should feed into a deployment pipeline and send the jar across
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
	
	private void shutdownMonitorAndApplication(Monitor monitor) {
		log.info("Stopping heartbeat with " + monitor.getName() + " as I'm about to kill it.");
		heartbeatService.stopHeartBeat(monitor);
		deploymentService.killMonitorAndApplication(monitor); 
		try {
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
	
	//TODO: at the moment just chooses another free one
	private void setNewApplicationAndMonitorLocation(Monitor monitor) {
		log.info("Updating location for " + monitor.getName());
		Application app = monitor.getApplication();
		Location newLocation = locationDecision.selectNew(app);
		if (newLocation == null) {
			log.error("No locations available! Cannot set new location for " + monitor.getName() + " so reverting to old one.");
			newLocation = locationService.findForApplication(app.getName());
		}
		app.setLocation(newLocation);
		appService.save(app);
		log.info("New location for " + app.getName() + " is " + newLocation.toString());
		int port = newLocation.getPort() + 10; //TODO: assumes any port can be used where application is being deployed.
		Location locationForMonitor = new Location(newLocation.getPath(), newLocation.getType(), port);
		locationService.save(locationForMonitor);
		monitor.setLocation(locationForMonitor);
		log.info("New location for " + monitor.getName() + " is " + locationForMonitor.toString());
		monitoringAgentService.save(monitor);
	}
	
	public Monitor generateMonitor(Application application) throws NoAvailableLocationException {
		
		selectLocation(application);
		Location loc = application.getLocation();
		if (loc == null) {
			throw new NoAvailableLocationException(application.getName());
		}
		// Use the location of the application, but select a different port 
		int port = loc.getPort() + 10; //TODO: assumes any port can be used where application is being deployed.
		Location locationForMonitor = new Location(loc.getPath(), loc.getType(), port);
		locationService.save(locationForMonitor);
		
		Monitor monitor = new Monitor(application.getName());
		monitor.setResponsibility(application);
		monitor.setLocation(locationForMonitor);
		monitoringAgentService.save(monitor);
		
		return monitor;
	}

	private void selectLocation(Application application) throws NoAvailableLocationException {
		Location loc = locationService.findForApplication(application.getName());
		if (loc == null) {
			//Select location. Bind application to it
			loc = locationDecision.selectInitial(application);
			if (loc == null) {
				throw new NoAvailableLocationException(application.getName());
			}
			application.setLocation(loc);
			appService.save(application);
		} 
	}

	public Monitor getMonitorForApplication(String name) {
		return monitoringAgentService.findForApplication(name, 1);
	}

	public void beginHeartbeat(Monitor monitor) {
		heartbeatService.startHeartBeat(monitor);
	}

	public void shutdownAndRedeploy(String agentName) {
		//Load in entities
		Monitor monitor = monitoringAgentService.findByName(agentName);
		Application app = appService.findByMonitor(agentName);
		monitor.setResponsibility(app);
		shutdownMonitorAndApplication(monitor);
		
		setNewApplicationAndMonitorLocation(monitor);
		log.info("Deploying monitor " + monitor.getName() + " to new location " + monitor.getLocation().toString());
		
		deployMonitor(monitor);
		beginHeartbeat(monitor);
	}
	
	public void duplicatedAndDeploy(String agentName) {
		//Load in entities
		Application app = appService.findByMonitor(agentName);
		
		String newApplicationName = app.getName() + "-duplicate";
		log.info("Created new application " + newApplicationName);
		
		Application newApplication = new Application(newApplicationName, app.getJarName());
		Monitor newMonitor;
		try {
			newMonitor = generateMonitor(newApplication);
		} catch (NoAvailableLocationException e) {
			log.error("Could not generate monitor for " + newApplicationName + " as no locations available.");
			return;
		}
		log.info("Created new monitor " + newMonitor.getName());
		
		log.info("Deploying new monitor " + newMonitor.getName());
		
		deployMonitor(newMonitor);
		beginHeartbeat(newMonitor);
	}

	public void killAll() {
		Collection<Monitor> monitors = monitoringAgentService.findAll(10);
		log.info("Found " + monitors.size() + " to kill");
		for (Monitor m: monitors) {
			shutdownMonitorAndApplication(m);
		}
	}
	
}
