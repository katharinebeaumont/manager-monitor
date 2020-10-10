package agent.memory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import agent.memory.domain.Application;
import agent.memory.domain.Location;
import agent.memory.domain.Monitor;

/*
 * Interface for the Agents to retrieve information from the databases
 */
@Service
public class DBInterface {
	
	@Autowired
	private ApplicationEntityService appService;
	
	@Autowired
	private LocationEntityService locationService;

	@Autowired
	private MonitoringEntityService monitoringAgentService;
	
	/*
	 * Called to retrieve all applications in the DB
	 * plus their associated locations
	 */
	public Collection<Application> getApplications() {
		Collection<Application> applications = appService.graphApplications(100);
		for (Application e: applications) {
			Location loc = locationService.findForApplication(e);
			if (loc != null) {
				e.setLocation(loc);
			}
		}
		return applications;
		
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

	/*
	 * Update application to set location
	 */
	public void updateLocationForApplication(Application e, Location loc) {
		e.setLocation(loc);
		appService.save(e);
	}
	
	/*
	 * Get monitor assigned to application
	 */
	public Monitor getMonitorForApplication(Application e) {
		return monitoringAgentService.findForApplication(e.getName(), 1);
	}

	/*
	 * Save monitor and location
	 */
	public void saveNewMonitor(Monitor monitor) {
		Location loc = monitor.getLocation();
		
		//If it already exists, discard new one
		Location locationForMonitor = locationService.findForDetails(loc.getPath(), loc.getPort());
		if (locationForMonitor != null) {
			monitor.setLocation(locationForMonitor);
		} else {
			locationService.save(loc);
		}
		
		//Delete old location relationship TODO: check this, what is it doing?
		locationService.deleteRelationship(monitor.getName());
		
		monitoringAgentService.save(monitor);
	}

	public Monitor getMonitor(String agentID) {
		Monitor m = monitoringAgentService.findByAgentId(agentID);
		updateLocationForMonitor(m);
		return m;
	}
	
	public Monitor updateLocationForMonitor(Monitor m) {
		Location loc = locationService.findForMonitor(m);
		if (loc != null) {
			m.setLocation(loc);
		}
		return m;
	}
	
	public Application getApplicationForMonitor(String agentID) {
		Application a = appService.findByMonitor(agentID);
		Location loc = locationService.findForApplication(a);
		if (loc != null) {
			a.setLocation(loc);
		}
		return a;
	}

	public void removeMonitor(String agentID) {
		locationService.deleteRelationship(agentID);
		monitoringAgentService.delete(agentID);
	}
}
