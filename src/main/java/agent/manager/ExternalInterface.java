package agent.manager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import agent.common.HeartbeatException;
import agent.common.HeartbeatRestController;
import agent.deployment.DeploymentService;
import agent.memory.domain.Application;
import agent.memory.domain.Location;
import agent.memory.domain.Monitor;

/**
 * Calls to other agents and applications
 */
@Service
public class ExternalInterface {

	@Autowired
	HeartbeatRestController hbController;
	
	@Autowired
	DeploymentService deploymentService;

	public boolean isDeployed(Application e) {
		Location loc = e.getLocation();
		if (loc != null) {
			try {
				hbController.beat(e.getName(), loc);
				return true;
			} catch (HeartbeatException he) {
				//Do nothing
			}
		}
		return false;
	}
	
	public void startMonitor(Monitor m) {
		deploymentService.startMonitor(m);
	}
	
	public void stopMonitor(Monitor m) {
		deploymentService.killMonitor(m);
	}
	
	public void stopApplication(Application app) {
		deploymentService.killApplication(app);
	}
}
