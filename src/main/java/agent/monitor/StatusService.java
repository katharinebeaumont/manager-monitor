package agent.monitor;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import agent.monitor.metrics.EventStore;

/**
 * TODO: write different status types e.g.
 *  - 0. Off
 *  - 1. Running
 *  - 2. Error? 
 *
 */
@Service
public class StatusService {

	private static final Logger log = LoggerFactory.getLogger(StatusService.class);
	
	private EventStore eventStore = new EventStore();
	
	private String status = "up and running";
	private String info;
	boolean activeHeartBeat;
	
	public String query() {
		//Get the names of events currently in the store
		Set<String> eventTypes = eventStore.getEventNames();
		
		info = "";
		boolean error = false;
		
		for (String type: eventTypes) {
			String value = eventStore.removeFirst(type);
			
			if (!value.isEmpty()) {
				error = analyse(type, value);
				if (!info.isEmpty())
					info += "|";
				
				info += type + ":" + value;
			}
		}
		buildStatus(error);
		
		return status + ":" + info;
	}
	
	/*
	 * Add the latest event to the event store
	 */
	public void update(String event) {
		//Add the latest event to the event store
		eventStore.add(event);
	}
	
	/*
	 * Add the latest event to the event store
	 */
	public void heartbeat() {
		activeHeartBeat = true;
	}
	
	/*
	 * Add the latest event to the event store
	 */
	public void stoppedHeartbeat() {
		activeHeartBeat = false;
	}
	
	//TODO: continue
	private boolean analyse(String type, String value) {
		//This would ideally be done as part of configurable rules... 
		boolean error = false;
		if (type.equals("jvm.memory.used")) {
			Double valueD = Double.parseDouble(value);
			
		}
		return error;
		
	}

	private void buildStatus(boolean error) {
		if (!activeHeartBeat) {
			status = "0"; //Down
		} else if (error) {
			status = "1"; //Error
		} else {
			status = "2"; //Up and running
		}
	}
}
