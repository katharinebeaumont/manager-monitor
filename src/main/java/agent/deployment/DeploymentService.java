package agent.deployment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import agent.memory.domain.Location;
import agent.memory.domain.Monitor;

/**
 * Eventually this will accommodate remote deployments to different hosts 
 * (e.g. Heroku, AWS)
 *
 */
@Service
public class DeploymentService {

	@Autowired
	LocalDeploymentService localDeployment;
	
	public void deploy(Monitor monitor, Location location) {
		
		if (location.getType().equals("local")) {
			localDeployment.deploy(monitor, location);
		}
	}
}
