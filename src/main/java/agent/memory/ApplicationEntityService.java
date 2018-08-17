package agent.memory;

import java.util.Collection;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import agent.memory.domain.Application;

@Service
public class ApplicationEntityService {
	
	@Autowired
	private ApplicationRepository applicationRepository;
	
    @Transactional(readOnly = true)
    public Application findByName(String name) {
        return applicationRepository.findByName(name);
    }
    
    @Transactional(readOnly = true)
    public Application findByMonitor(String monitorName) {
        return applicationRepository.findByMonitor(monitorName, 1);
    }

    @Transactional(readOnly = true)
    public Collection<Application> graph(int limit) {
        Collection<Application> result = applicationRepository.graph(limit);
        return result;
    }
    
    @Transactional(readOnly = true)
    public Collection<Application> graphApplications(int limit) {
    		Collection<Application> result = applicationRepository.graphApplication(limit);
    		return result;
    }
    
    @Transactional
    public void deleteAll() {
    		applicationRepository.deleteAll();
    }
    
    @Transactional
    public void save(Application entity) {
    		applicationRepository.save(entity);
    }
	

}
