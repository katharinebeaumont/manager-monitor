package agent.common;

import agent.memory.domain.Location;

/**
 * Interface to allow agents to speak to other agents/ applications
 */
public interface Heartbeat {

	Location getLocation();
	
	String getName();
	
	String getPid();
}
