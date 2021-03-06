package agent.manager;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;

import agent.memory.DBInterface;
import agent.memory.domain.Application;
import agent.memory.domain.Monitor;

/**
 * Oversees all applications
 * Starting point for the managing agent.
 * 
 *
 */
@Controller
public class ManagerAgent {

	private static final Logger log = LoggerFactory.getLogger(ManagerAgent.class);
	
	@Autowired
	CommandControllerManager commandCentre;
	
	@Autowired
	MonitoringCentre monitoringCentre;
	
	public void startup() {
		
		//Read in all applications the Manager is responsible for, and their locations
		Collection<Application> col = commandCentre.getApplications();
		log.info("Starting " + col.size() + " applications");
		for (Application e : col) { 
			//Check if applications are deployed, if not, deploy
			Monitor m = commandCentre.deploy(e);
			//And start feedback
			monitoringCentre.begin(m);
		}
	}
	
	
}
