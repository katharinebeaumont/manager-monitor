package agent.deployment;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

	private static final Logger log = LoggerFactory.getLogger(DeploymentService.class);
	
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
		        + " -Dagent.mode=monitor -Dlogging.file=" 
				+ agentLoggingFile + " -Dagent.name=" + monitor.getName() + " "
		        + agentJar;
		
		if (location.getType().equals("local")) {
			//Create subfolder in directory, copy Jar file there
			String newDir = agentDirectory + monitor.getName();
			String targetDir = newDir + "/" + agentJar;
			File f = new File(newDir);
			if (!f.exists() && f.mkdirs()) {
				log.info("Created " + newDir);
			} else if (f.exists()){
				log.info("Not creating " + newDir + " as already exists");
			} else {
				log.error("Error creating " + newDir);
			}
			
			Path source =  Paths.get(agentDirectory + agentJar);
			Path target =  Paths.get(targetDir);
			try {
				Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
			
			} catch (IOException e) {
				log.error("Error copying from " + source + " to " + target);
			}
			
			localDeployment.executeCommand(newDir, command);
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
		
		if (location.getType().equals("local")) {
			//Create subfolder in directory, copy Jar file there
			String newDir = agentDirectory + application.getName();
			String targetDir = newDir + "/" + application.getJarName();
			File f = new File(newDir);
			if (!f.exists() && f.mkdirs()) {
				log.info("Created " + newDir);
			} else if (f.exists()){
				log.info("Not creating " + newDir + " as already exists");
			} else {
				log.error("Error creating " + newDir);
			}
			
			Path source = Paths.get(agentDirectory + application.getJarName());
			Path target = Paths.get(targetDir);
			try {
				Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
			
			} catch (IOException e) {
				log.error("Error copying from " + source + " to " + target);
			}
			
			localDeployment.executeCommand(newDir, command);
		}
	}

	public void killMonitor(Monitor monitor) {
		String monitorIdentifier = monitor.getPid();
		Location monitorLoc = monitor.getLocation();
		int monitorPort = monitorLoc.getPort();
		
		if (monitorIdentifier.isEmpty()) {
			log.error("Error! Could not kill " + monitor.getName() + " with PID as I don't have it.");
			log.error("Trying to kill it by port instead but this is dangerous and doesn't always work!");
			monitorIdentifier = "$(ps -e | grep Dserver.port=" + monitorPort + "| awk '{print $1}')";
		}
		
		String killMonitorCommand = "kill " + monitorIdentifier;
		
		log.info("Executing kill command: " + killMonitorCommand);
		localDeployment.executeCommand(agentDirectory, killMonitorCommand);
	}
	
	public void killApplication(Application app) {
		String pid = app.getPid();
		Location appLoc = app.getLocation();
		int appPort = appLoc.getPort();
		
		String killApplicationCommand = "kill $(ps -e | grep Dserver.port=" + appPort + "| awk '{print $1}')";
		if (pid != null && !pid.isEmpty()) {
			killApplicationCommand = "kill " + pid;
		} else {
			log.error("Error! No PID for " + app.getName() + " in the database.");
			log.error("Trying to kill it by name instead but this is dangerous and doesn't always work!");
		}
		
		log.info("Executing kill command: " + killApplicationCommand);
		localDeployment.executeCommand(agentDirectory, killApplicationCommand);
	}
}
