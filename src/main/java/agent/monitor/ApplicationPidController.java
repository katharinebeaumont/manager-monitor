package agent.monitor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import agent.common.HeartbeatException;
import agent.common.PidRestController;
import agent.memory.ApplicationEntityService;
import agent.memory.domain.Application;
import agent.memory.domain.Location;

@Component
public class ApplicationPidController {

	@Autowired
    private PidRestController pidController;

	@Autowired
	private ApplicationEntityService appService;

	/*
	 * Get the pid from the application
	 * Strip out JSON and save
	 */
	public void getAppPid(Application application) throws HeartbeatException {
		String pid = pidController.requestPid(application.getName(), application.getLocation());
		pid = pid.replaceAll("[^\\d]", "");
		application.setPid(pid);
		appService.save(application);
	}
}
