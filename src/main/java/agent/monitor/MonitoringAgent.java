package agent.monitor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import agent.loadmanager.ManagerAgent;
import agent.memory.ApplicationEntityService;
import agent.memory.domain.Application;

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
	
	@Value("${agent.name}")
	private String name;
	
	public void startup() {
		log.info("Starting monitoring agent. My name is " + name);
		//Check what have been assigned in Neo4J graph and start up
		log.info("Figuring out who I am responsible for.");
		Application application = appService.findByMonitor(name);
		log.info("Initialising " + application.getName());
	}
}
