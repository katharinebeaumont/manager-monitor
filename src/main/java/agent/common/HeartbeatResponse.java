package agent.common;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import agent.monitor.StatusService;


@Component
public class HeartbeatResponse {

	@Autowired 
	private StatusService status;
	
	public String getResponse() {
		return status.query();
	}
}
