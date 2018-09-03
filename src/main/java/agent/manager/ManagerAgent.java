package agent.manager;

import java.util.Collection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import agent.common.HeartbeatService;
import agent.deployment.DeploymentService;
import agent.memory.ApplicationEntityService;
import agent.memory.LocationEntityService;
import agent.memory.MonitoringEntityService;
import agent.memory.domain.Application;
import agent.memory.domain.Location;
import agent.memory.domain.Monitor;

/**
 * The Manager Agent is responsible for allocating monitoring agents to applications that
 * need deploying. It then starts a heartbeat with each monitoring agent to check the status of 
 * the monitoring agent and the application it is responsible for. 
 * The heartbeat allows the manager to collect feedback and send instructions to the monitor 
 * based on the feedback.
 */
@Controller
public class ManagerAgent {

	private static final Logger log = LoggerFactory.getLogger(ManagerAgent.class);
	
	@Autowired
	private ApplicationEntityService appService;
	
	@Autowired
	private MonitoringEntityService monitoringAgentService;
	
	@Autowired
	private LocationEntityService locationService;
	
	@Autowired
	private LocationDecisionService locationDecision;
	
	@Autowired
	private HeartbeatService heartbeatService;
	
	@Autowired
	private DeploymentService deploymentService;
	
	/*
	 * Start up: read in memory, look at applications I need to start. These will be 
	 * applications in the database without monitoring agents.
	 * For each application to start, find or generate and deploy the monitoring agent, 
	 * and start a heartbeat with that agent.
	 */
	public void startup() {
		// Read in all the required applications
		log.info("Reading in required applications");
		Collection<Application> col = appService.graphApplications(100);
		log.debug("Collection is size " + col.size());
		
		for (Application e : col) {
			// Check if there is a monitor for the application
			Monitor monitor = monitoringAgentService.findForApplication(e.getName(), 1);
			log.info("Service is: " + e.getName());
			// If not, generate and deploy a new monitoring agent.
			if (monitor == null) {
				log.info("Generating a new agent for: " + e.getName());
				monitor = generateAndDeployMonitor(e);
			}
			
			// Start a heartbeat with the monitor
			heartbeatService.startHeartBeat(monitor);
		}
	}

	private Monitor generateAndDeployMonitor(Application application) {
		
		Location locationForMonitor = selectLocation(application);
				
		Monitor monitor = new Monitor(application.getName());
		monitor.setResponsibility(application);
		monitor.setLocation(locationForMonitor);
		monitoringAgentService.save(monitor);
		
		deploymentService.deployMonitor(monitor);
		return monitor;
	}

	private Location selectLocation(Application application) {
		Location loc = locationService.findForApplication(application.getName());
		if (loc == null) {
			//Select location. Bind application to it
			loc = locationDecision.select(application);
			application.setLocation(loc);
			appService.save(application);
		} 
		// Use the location of the application, but select a different port 
		int port = loc.getPort() + 10; //TODO: assumes any port can be used where application is being deployed.
		Location locationForMonitor = new Location(loc.getPath(), loc.getType(), port);
		locationService.save(locationForMonitor);
		return locationForMonitor;
	}
}
