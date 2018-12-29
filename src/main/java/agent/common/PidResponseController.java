package agent.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PidResponseController {
	
	private static final Logger log = LoggerFactory.getLogger(PidResponseController.class);
	
	@Autowired
	private PidResponse response;
	
	@RequestMapping("/pid")
	@ResponseBody
	public PidResponse getPid() {
		log.debug("Replying to pid request");
		return response;
	}

}
