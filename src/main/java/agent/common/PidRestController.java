package agent.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import agent.memory.domain.Location;

@RestController
public class PidRestController {
	
	private static final Logger log = LoggerFactory.getLogger(PidRestController.class);
	
	@Autowired
    private RestTemplate restTemplate;
	
	public String requestPid(String name, Location loc) throws HeartbeatException {
		String url = loc.getPath() + ":" + loc.getPort() + "/pid";
        log.info("Requesting PID from " + name + " on port " + loc.getPort());
        log.debug("URL: " + url);
        try {
        		String response = restTemplate.getForObject(url, String.class);
        		log.info("Response from " + name + " is :" + response);
        		return response;
        } catch (RestClientException ex) {
        		log.error("Error calling " + name);
        		log.error(ex.getMessage());
        		throw new HeartbeatException("Error with PID for " +  name);
        }
	}
}
