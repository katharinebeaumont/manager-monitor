package agent.manager;


import java.util.List;
import java.util.Collection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
@RestController
public class ManagerAgent {

	private static final Logger log = LoggerFactory.getLogger(ManagerAgent.class);
	
	@Autowired
	private ApplicationEntityService appService;
	
	@Autowired
	private ManagerAgentActions actions;
	
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
			// Check if there is a location for the application
			if (e.getLocation() == null) {
				//if not, set one
				List<Location> locations = actions.getAvailableLocations();
				Location loc = locations.get(0);
				log.info("Setting location for " + e.getName() + " of " + loc.toString());
				e.setLocation(loc);
				appService.save(e);
			}
			// Check if there is a monitor for the application
			Monitor monitor = actions.getMonitorForApplication(e.getName());
						
			// If not, generate and deploy a new monitoring agent.
			if (monitor == null) {
				log.info("Generating a new agent for " + e.getName());
				try {
					monitor = actions.generateMonitorAndAssignLocation(e);
					actions.deployMonitor(monitor);
				} catch (NoAvailableLocationException ex) {
					log.error("Could not generate monitor for " + e.getName() + " as no locations available.");
					return;
				}
			} else {
				actions.startMonitorIfNotRunning(monitor);
			}
			
			actions.beginHeartbeatAndGetPid(monitor);
		}
	}
}
