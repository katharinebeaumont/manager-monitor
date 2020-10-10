package agent.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import agent.memory.domain.Location;

/**
 * Rest Controller that sends a request to monitoring agents and collects feedback.
 */
@RestController
public class HeartbeatRestController {
	
	private static final Logger log = LoggerFactory.getLogger(HeartbeatRestController.class);
	
	@Autowired
    private RestTemplate restTemplate;
	
	public String beat(String name, Location loc) throws HeartbeatException {
		
		String url = loc.getPath() + ":" + loc.getPort() + "/heartbeat";
        log.debug("Checking on " + name + " on port " + loc.getPort());
        log.debug("URL: " + url);
        try {
    		String response = restTemplate.getForObject(url, String.class);
    		log.debug(name + " heartbeat response is :" + response);
    		return response;
        } catch (RestClientException ex) {
    		log.error("Error calling " + name);
    		log.error(ex.getMessage());
    		throw new HeartbeatException("Error with heartbeat for " +  name);
        }
	}
	
}

