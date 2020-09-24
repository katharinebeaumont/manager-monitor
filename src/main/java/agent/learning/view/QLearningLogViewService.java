package agent.learning.view;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class QLearningLogViewService {

	private static final Logger log = LoggerFactory.getLogger(QLearningLogViewService.class);
	
	@Value("${logging.file}")
	private String logFileName;
	
	public QLearningLogView build(String agentName) {
		//read in log file line by line
		log.info("Reading in " + logFileName);
		List<String> logLines = new ArrayList<String>();
		try (BufferedReader br = new BufferedReader(new FileReader(logFileName))) {

			String line;
			while ((line = br.readLine()) != null) {
				process(line, agentName, logLines);
			}

		} catch (IOException e) {
			log.error("Could not read " + logFileName);
			e.printStackTrace();
		}
		

		QLearningLogView logView = new QLearningLogView(logLines);
		//if line contains agent name, strip out date, time, and log message
		return logView;
		
	}
	
	public void process(String line, String agentName, List<String> logLines) {
		if (line.contains(agentName)) {
			String[] logArray = line.split(" ");
			String dateTime = logArray[0] + " " + logArray[1];
			String remainingMessage = line.substring(dateTime.length());
			String message = remainingMessage.substring(remainingMessage.indexOf(":"));
			logLines.add(dateTime + message);
		}
	}
}
