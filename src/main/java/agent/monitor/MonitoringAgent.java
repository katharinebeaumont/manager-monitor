package agent.monitor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import agent.common.HeartbeatService;
import agent.deployment.DeploymentService;
import agent.memory.ApplicationEntityService;
import agent.memory.LocationEntityService;
import agent.memory.domain.Application;
import agent.memory.domain.Location;

/**
 * If started in monitoring mode, the agent is a monitor that starts up the application
 * it is responsible for locally on a different port.
 * It replies to heartbeats from the manager agent with status updates.
 * TODO: mechanism where if don't hear from manager (stop receiving heartbeats) have a facility
 *  to elect and promote a monitoring agent to manager. Required: access to Neo4J.
 */
@Controller
public class MonitoringAgent {

	private static final Logger log = LoggerFactory.getLogger(MonitoringAgent.class);
		
	@Autowired
	private ApplicationEntityService appService;

	@Autowired
	private LocationEntityService locationService;
	
	@Autowired
	private DeploymentService deploymentService;
	
	@Autowired
	private HeartbeatService heartbeatService;
	
	@Value("${agent.name}")
	private String name;
	
	public void startup() {
		log.info("Starting monitoring agent. My name is " + name);
		//Check what have been assigned in Neo4J graph and start up
		log.info("Figuring out who I am responsible for.");
		Application application = loadApplication();
		log.info("Initialising " + application.getName());
		deploymentService.deployApplication(application);
		log.info("Starting heartbeat with " + application.getName());
		heartbeatService.startHeartBeat(application);
	}

	private Application loadApplication() {
		Application application = appService.findByMonitor(name);
		Location location = locationService.findForApplication(application.getName());
		application.setLocation(location);
		return application;
	}
}
