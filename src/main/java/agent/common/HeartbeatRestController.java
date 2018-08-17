package agent.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import agent.memory.domain.Location;
import agent.memory.domain.Monitor;

/**
 * Rest Controller that sends a request to monitoring agents and collects feedback.
 */
@RestController
public class HeartbeatRestController {
	
	private static final Logger log = LoggerFactory.getLogger(HeartbeatRestController.class);
	
	@Autowired
    private RestTemplate restTemplate;
	
	public void beat(String name, Location loc) throws Exception {
		
		//TODO set a timer around this
        String url = loc.getPath() + ":" + loc.getPort() + "/heartbeat";
        log.debug("Checking on " + name + " on port " + loc.getPort());
        log.info("URL: " + url);
        try {
        		HeartbeatResponse response = restTemplate.getForObject(url, HeartbeatResponse.class);
        		log.debug("Response is :" + response);
        } catch (RestClientException ex) {
        		log.error("Error calling " + name);
        		ex.printStackTrace();
        		throw new Exception("Error with heartbeat for " +  name);
        }
	}
	
}

