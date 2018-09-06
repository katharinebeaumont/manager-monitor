package agent.deployment;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import agent.memory.domain.Application;
import agent.memory.domain.Location;
import agent.memory.domain.Monitor;

/**
 * Deploy the monitoring agent locally from the jar in the agent directory.
 */
@Service
public class LocalDeploymentService {

	
	private static final Logger log = LoggerFactory.getLogger(LocalDeploymentService.class);
	
	public void executeCommand(String directory, String commandStr) {

		ProcessBuilder builder = new ProcessBuilder();
		//This needs to be split into a String array
		String[] command = commandStr.split(" ");
		builder.command(command);
		builder.directory(new File(directory)); 
		log.info("Executing at " + directory + " with command: " + commandStr);
		
		// This is for debugging: either hijack the manager's console output (logtoconsole)
		// or log the output to the file in the directory specified when the monitoring agent 
		// was started (agentLoggingFile; agentDirectory).
		boolean logtoconsole = false;
		if (logtoconsole) {
			Process process;
			try {
				// Start then write output to the manager's console
				process = builder.start();
				StreamConsumer streamGobbler = 
						  new StreamConsumer(process.getInputStream(), System.out::println);
				Executors.newSingleThreadExecutor().submit(streamGobbler);
				
				log.info("Initialised application.");
			} catch (IOException e1) {
				log.error("Error loading application. Stack trace is:");
				e1.printStackTrace();
			}
		} else {
			try {
				// Start then pretty much abandon
				builder.start();
				log.info("Initialised service.");
			} catch (IOException e1) {
				log.error("Error loading monitor. Stack trace is:");
				e1.printStackTrace();
			}
		}
	}
}
