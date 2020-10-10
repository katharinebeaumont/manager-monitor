package agent.memory;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import agent.memory.domain.Monitor;

@Service
public class MonitoringEntityService {

	@Autowired
	private MonitorRepository monitorRepository;

    @Transactional(readOnly = true)
    public Monitor findByAgentId(String agentId) {
        return monitorRepository.findByName(agentId);
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
    
    @Transactional(readOnly = true)
    public Collection<Monitor> findAll(int limit) {
		Collection<Monitor> result = monitorRepository.getAll(limit);
		return result;
    }

    @Transactional
	public void delete(String agentID) {
		Monitor m = monitorRepository.findByName(agentID);
		monitorRepository.delete(m);
	}
    
}
