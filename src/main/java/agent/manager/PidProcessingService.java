package agent.manager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import agent.common.Heartbeat;
import agent.memory.MonitoringEntityService;
import agent.memory.domain.Monitor;

@Service
public class PidProcessingService {
	
	private static final Logger log = LoggerFactory.getLogger(PidProcessingService.class);
	
	@Autowired
	private MonitoringEntityService monitoringAgentService;
	
	public void processPid(Heartbeat heartbeat, String pid) {
		pid = pid.replaceAll("[^\\d]", "");
		
		if (heartbeat instanceof Monitor) {
			Monitor m = (Monitor)heartbeat;
			m.setPid(pid);
			monitoringAgentService.save(m);
			log.info("Saved pid " + pid + " for " + heartbeat.getName());
		} else 	{
			log.info("Could not save pid " + pid + " for " + heartbeat.getName());
			log.error("Not implemented");
		}
	}
	
}
