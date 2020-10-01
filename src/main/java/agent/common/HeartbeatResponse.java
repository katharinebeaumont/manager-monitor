package agent.common;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import agent.monitor.metrics.MonitorEventStore;


@Component
public class HeartbeatResponse {

	@Autowired 
	private MonitorEventStore eventStore;
	
	public String getResponse() {
		return eventStore.removeFirst().toString();
	}
}
