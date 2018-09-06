package agent.monitor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import agent.memory.domain.Location;

/**
 * Rest Controller that sends a request from monitoring agents to the
 * service they manage and collects feedback.
 */
@RestController
public class MonitoringRestController {

	private static final Logger log = LoggerFactory.getLogger(MonitoringRestController.class);
	
	@Autowired
    private RestTemplate restTemplate;
	
	@Autowired
	private StatusService status;
	
	public void monitor(String name, Location loc, String endpoint) throws Exception {
		
		String url = loc.getPath() + ":" + loc.getPort() + "/" + endpoint;
        log.debug("Harvesting from " + name + " on port " + loc.getPort());
        log.debug("URL: " + url);
        try {
       		String response = restTemplate.getForObject(url, String.class);
       		log.debug("Monitoring response is :" + response);
       		status.update(response);
         } catch (RestClientException ex) {
       		log.error("Error calling " + name);
       		ex.printStackTrace();
       		status.lostContact();
       		throw new Exception("Error with monitoring " +  name);
        }
	}
}
	