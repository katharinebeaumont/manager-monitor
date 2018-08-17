package agent.manager;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import agent.memory.LocationEntityService;
import agent.memory.domain.Application;
import agent.memory.domain.Location;

/**
 * This is the decision making faculty of the Manager when deciding where to deploy an
 * application. 
 * At present it isn't sophisticated (just return the first available location). Eventually
 * this will bring in what has been learnt from experience about the application, and the type
 * of location it needs.
 */
@Service
public class LocationDecisionService {

	@Autowired
	private LocationEntityService locationService;

	public Location select(Application e) {
		// Look at all free locations
		Collection<Location> locs = locationService.findFreeLocation();
		// Currently, just return the first one.
		for (Location l: locs) {
			return l; 
		}
		return null;
	}
	
	
}
