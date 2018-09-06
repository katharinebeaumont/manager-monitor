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
	
	@Value("${server.port}")
	protected String currentPort;
	
	public void startMonitor(Monitor monitor) {
		
		Location location = monitor.getLocation();
		String agentLoggingFile = monitor.getName() + monitor.getBorn() + "-log.txt";
		String command = "java -jar -Dserver.port=" + location.getPort() 
		        + " -Dagent.mode=monitoring -Dlogging.file=" 
				+ agentLoggingFile + " -Dagent.name=" + monitor.getName() + " "
		        + agentJar;
		
		if (location.getType().equals("local")) {
			localDeployment.executeCommand(agentDirectory, command);
		}
	}
	
	public void deployApplication(Application application) {
		
		Location location = application.getLocation();
		String name = application.getName();
		
		String loggingFile = application.getName() + "-log.txt";
		String command = "java -jar -Dserver.port=" + location.getPort() 
		        + " -Dlogging.file=" + loggingFile
		        + " -Dmonitoring.port=" + currentPort 
		        + " -Dname=" + name
		        + " " + application.getJarName();
		
		// Applications are always deployed locally
		localDeployment.executeCommand(agentDirectory, command);
	}

	public void killMonitorAndApplication(Monitor monitor) {
		String killMonitorCommand = "kill $(ps -e | grep " + monitor.getName() + ")";
		localDeployment.executeCommand(agentDirectory, killMonitorCommand);
		//TODO: need to load applications in
		Application app = monitor.getApplication();
		String killApplicationCommand = "kill $(ps -e | grep " + app.getName() + ")";
		localDeployment.executeCommand(agentDirectory, killApplicationCommand);
	}
	
	
}
