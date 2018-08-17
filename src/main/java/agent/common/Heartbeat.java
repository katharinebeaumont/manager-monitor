package agent.common;

import agent.memory.domain.Location;

public interface Heartbeat {

	Location getLocation();
	
	String getName();
}
