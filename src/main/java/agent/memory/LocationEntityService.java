package agent.memory;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import agent.AgentApplication;
import agent.memory.domain.Application;
import agent.memory.domain.Location;
import agent.memory.domain.Monitor;

@Service
public class LocationEntityService {

	@Autowired
	private LocationRepository locationRepository;

	@Autowired
	private LocationRelationshipRepository locationRelationshipRepository;
	
	private static final Logger log = LoggerFactory.getLogger(LocationEntityService.class);
	
	
    @Transactional(readOnly = true)
    public Location findForApplication(Application application) {
		return locationRepository.locationForApplication(application.getName(), 1);
    }
    
    @Transactional(readOnly = true)
    public Location findForMonitor(Monitor monitor) {
		return locationRepository.locationForMonitor(monitor.getName(), 1);
    }
    
    @Transactional(readOnly = true)
    public Location findForDetails(String path, int port) {
		return locationRepository.locationForPathPort(path, port);
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
    
    @Transactional
    public void deleteRelationship(String entity) {
		locationRelationshipRepository.deleteRelationship(entity);
    }
	
    
}
