package agent.memory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import agent.memory.domain.Application;
import agent.memory.domain.Monitor;

@Service
public class MonitoringEntityService {

	@Autowired
	private MonitorRepository monitorRepository;

    @Transactional(readOnly = true)
    public Monitor findByName(String name) {
        return monitorRepository.findByName(name);
    }
	
    @Transactional(readOnly = true)
    public Monitor findForApplication(String name, int limit) {
        return monitorRepository.monitorForApplication(name, limit);
    }
    
    @Transactional
    public void save(Monitor entity) {
    		monitorRepository.save(entity);
    }
    
    @Transactional
    public void deleteAll() {
    		monitorRepository.deleteAll();
    }
    
}
