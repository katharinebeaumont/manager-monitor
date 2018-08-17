package agent.common;

/**
 * TODO: heartbeat response should incorporate the monitoring agent Status.
 */
public class HeartbeatResponse {

	//TODO: move responses to configuration file?
	private String response = "All's well";
	
	public String getResponse() {
		return response;
	}
	
	@Override
	public String toString() {
		return response;
	}
}
