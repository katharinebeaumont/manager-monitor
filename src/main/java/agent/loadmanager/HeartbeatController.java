package agent.loadmanager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import agent.common.HeartbeatResponse;
import agent.memory.domain.Location;
import agent.memory.domain.Monitor;

/**
 * Rest Controller that sends a request to monitoring agents and collects feedback.
 */
@RestController
public class HeartbeatController {
	
	private static final Logger log = LoggerFactory.getLogger(HeartbeatController.class);
	
	@Autowired
    private RestTemplate restTemplate;
	
	public void beat(Monitor monitor, Location loc) throws Exception {
		
		//TODO set a timer around this
        String url = loc.getPath() + ":" + loc.getPort() + "/heartbeat";
        log.debug("Checking on " + monitor.getName() + " on port " + loc.getPort());
        log.info("URL: " + url);
        try {
        		HeartbeatResponse response = restTemplate.getForObject(url, HeartbeatResponse.class);
        		log.debug("Response is :" + response);
        } catch (RestClientException ex) {
        		log.error("Error calling " + monitor.getName());
        		ex.printStackTrace();
        		throw new Exception("Error with heartbeat for " +  monitor.getName());
        }
	}
	
}

