package agent.memory;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import agent.memory.domain.Application;
import agent.memory.domain.Location;
import agent.memory.domain.Monitor;

@Service
public class LocationEntityService {

	@Autowired
	private LocationRepository locationRepository;

    @Transactional(readOnly = true)
    public Location findForApplication(String applicationName) {
    		return locationRepository.locationForApplication(applicationName, 1);
    }
    
    @Transactional(readOnly = true)
    public Location findForMonitor(String monitorName) {
    		return locationRepository.locationForMonitor(monitorName, 1);
    }
    
    @Transactional(readOnly = true)
    public Collection<Location> findFreeLocation() {
    		return locationRepository.freeLocation(5);
    }
    
    @Transactional
    public void save(Location loc) {
    		locationRepository.save(loc);
    }
    
    @Transactional
    public void deleteAll() {
    		locationRepository.deleteAll();
    }
    
}
