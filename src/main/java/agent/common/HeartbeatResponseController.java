package agent.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Reply to the heartbeat.
 * TODO: monitor the heartbeat mapping and see if it hasn't been called for a certain
 * amount of time.
 */
@RestController
public class HeartbeatResponseController {

	private static final Logger log = LoggerFactory.getLogger(HeartbeatResponseController.class);
	
	@Autowired
	private HeartbeatResponse response;
	
	@RequestMapping("/heartbeat")
	@ResponseBody
	public HeartbeatResponse beat() {
		log.debug("Replying to heartbeat");
		return response;
	}
}
