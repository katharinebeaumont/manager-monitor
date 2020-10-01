package agent.manager;

import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import agent.memory.DBInterface;
import agent.memory.domain.Application;
import agent.memory.domain.Location;
import agent.memory.domain.Monitor;

/*
 * Deals with one application at time
 */
@Component
public class CommandControllerManager {

	private static final Logger log = LoggerFactory.getLogger(CommandControllerManager.class);
	
	@Autowired
	DBInterface dbInterface;
	
	@Autowired
	ExternalInterface exInterface;
	
	public Monitor deploy(Application e) {
		
		//Is there a location?
		Location appLoc = e.getLocation();
		if (appLoc == null) {
			//If not assign one
			List<Location> locations = dbInterface.getAvailableLocations();
			appLoc = locations.get(0);
			dbInterface.updateLocationForApplication(e, appLoc);
			log.info("Setting location for " + e.getName() + " of " + appLoc.toString());
		}
		
		//Is there a monitor? 
		Monitor monitor = dbInterface.getMonitorForApplication(e);

		if (exInterface.isDeployed(e)) {
			if (monitor != null) { 
				log.info("Not deploying " + e.getName() + " as it is already deployed.");
				return monitor;
			} else {
				log.error(e.getName() + " is deployed, but has no monitor. Creating a new one.");
			}
		}
		
		if (monitor == null) {
			//If not, create and assign one
			log.info("Creating a new monitoring agent for " + e.toString());
			monitor = new Monitor(e.getName());
			monitor.setResponsibility(e);
		}
		
		//Either way update the location and save
		Location monitorLoc = new Location(appLoc.getPath(), appLoc.getType(), appLoc.getPort() + 10, true);
		monitor.setLocation(monitorLoc);;
		log.info("Setting location for " + monitor.getName() + " of " + monitorLoc.toString());
		dbInterface.saveNewMonitor(monitor);
		
		//Start the monitor
		exInterface.startMonitor(monitor);
		return monitor;
	}

	public void shutdown(String agentID) {
		Application app = dbInterface.getApplicationForMonitor(agentID);
		exInterface.stopApplication(app);
		
		Monitor m = dbInterface.getMonitor(agentID);
		exInterface.stopMonitor(m);
	}

	public void relocate(String agentID, Location location) {
		shutdown(agentID);
		Application app = dbInterface.getApplicationForMonitor(agentID);
		dbInterface.updateLocationForApplication(app, location);
		deploy(app);
	}

	public Collection<Application> getApplications() {
		return dbInterface.getApplications();
	}

}
