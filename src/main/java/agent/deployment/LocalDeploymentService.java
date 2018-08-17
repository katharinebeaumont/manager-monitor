package agent.deployment;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import agent.loadmanager.StreamConsumer;
import agent.memory.domain.Location;
import agent.memory.domain.Monitor;

/**
 * Deploy the monitoring agent locally from the jar in the agent directory.
 */
@Service
public class LocalDeploymentService {

	@Value("${agent.directory}")
	protected String agentDirectory;
	
	@Value("${agent.monitor.jar}")
	protected String agentJar;

	private static final Logger log = LoggerFactory.getLogger(LocalDeploymentService.class);
	
	public void deploy(Monitor monitor, Location location) {

		ProcessBuilder builder = new ProcessBuilder();
		String agentLoggingFile = monitor.getName() + monitor.getBorn() + "-log.txt";
		builder.command("java", "-jar", "-Dserver.port=" + location.getPort(), "-Dagent.mode=monitoring", 
				"-Dagent.logging.file=" + agentLoggingFile, "-Dagent.name=" + monitor.getName(), agentJar);
		
		builder.directory(new File(agentDirectory)); 
		
		log.info("Initialising monitor at " + agentDirectory + " on port " + location.getPort());
		
		// This is for debugging: either hijack the manager's console output (logtoconsole)
		// or log the output to the file in the directory specified when the monitoring agent 
		// was started (agentLoggingFile; agentDirectory).
		boolean logtoconsole = false;
		if (logtoconsole) {
			Process process;
			try {
				// Start then pretty much abandon
				//File output = new File(agentDirectory, "console" + agentLoggingFile); 
				process = builder.start();
				//StreamConsumer streamGobbler = 
				//		  new StreamConsumer(process.getInputStream(), new FileConsumer(output));
				StreamConsumer streamGobbler = 
						  new StreamConsumer(process.getInputStream(), System.out::println);
				Executors.newSingleThreadExecutor().submit(streamGobbler);
				//int exitCode = process.waitFor();
				log.info("Initialised monitor service.");
			} catch (IOException e1) {
				log.error("Error loading monitor. Stack trace is:");
				e1.printStackTrace();
			}
		} else {
			try {
				// Start then pretty much abandon
				builder.start();
				log.info("Initialised monitor service.");
			} catch (IOException e1) {
				log.error("Error loading monitor. Stack trace is:");
				e1.printStackTrace();
			}
		}
	}
}
