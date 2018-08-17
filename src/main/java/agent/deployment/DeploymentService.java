package agent.deployment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import agent.memory.domain.Application;
import agent.memory.domain.Location;
import agent.memory.domain.Monitor;

/**
 * Eventually this will accommodate remote deployments to different hosts 
 * (e.g. Heroku, AWS)
 * Applications are always deployed locally by the monitoring agent
 */
@Service
public class DeploymentService {

	@Autowired
	LocalDeploymentService localDeployment;
	
	//TODO: think this might all be too specific to local deployments to be in here.
	@Value("${agent.directory}")
	protected String agentDirectory;
	
	@Value("${agent.monitor.jar}")
	protected String agentJar;

	@Value("${agent.service.jar}")
	protected String serviceJar;
	
	public void deployMonitor(Monitor monitor) {
		
		Location location = monitor.getLocation();
		Application app = monitor.getApplication();
		String agentLoggingFile = monitor.getName() + monitor.getBorn() + "-log.txt";
		String command = "java -jar -Dserver.port=" + location.getPort() 
		        + " -Dagent.mode=monitoring -Dlogging.file=" 
				+ agentLoggingFile + " -Dagent.name=" + monitor.getName() + " -Dagent.service.jar=" 
		        + app.getName() + ".jar " //TODO: assumes that the application is packaged as jar and naming convention used for jar
		        + agentJar;
		
		if (location.getType().equals("local")) {
			localDeployment.deploy(agentDirectory, command);
		}
	}
	
	public void deployApplication(Application application) {
		
		Location location = application.getLocation();
		
		String loggingFile = application.getName() + "-log.txt";
		String command = "java -jar -Dserver.port=" + location.getPort() 
		        + " -Dlogging.file=" + loggingFile + " " + serviceJar;
		
		// Applications are always deployed locally
		localDeployment.deploy(agentDirectory, command);
	}
	
	
}
