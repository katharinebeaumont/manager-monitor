package agent.monitor;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import agent.monitor.metrics.EventStore;

/**

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
			//String value = eventStore.removeFirst(type);
			String value = eventStore.removeLast(type);
			
			if (!value.isEmpty()) {
				error = analyse(type, value);
				String feedback = convert(type, value);
				if (!info.isEmpty())
					info += ";";
				
				info += type + ":" + feedback;
			}
		}
		buildStatus(error, info);
		String response = status;
		if (!info.isEmpty()) {
			response += ";" + info;
		} else if (info.isEmpty() && status.equals("-1")) {
			response += ";" + "system is down";
		}
		return response;
	}
	
	

	/*
	 * Add the latest event to the event store
	 */
	public void update(String event) {
		//Add the latest event to the event store
		eventStore.add(event);
		activeHeartBeat = true;
	}
	
	public void lostContact() {
		activeHeartBeat = false;
	}
	
	private boolean analyse(String type, String value) {
		//This would ideally be done as part of configurable rules... 
		boolean error = false;
		if (type.equals("jvm.memory.used")) {
			Double valueD = Double.parseDouble(value);
			if (valueD > new Double(1.5E8)) {
				error = true;
			}
		}
		if (type.equals("jvm.memory.max")) {
			Double valueD = Double.parseDouble(value);
			if (valueD < new Double(3E9)) {
				error = true;
			}
		}
		if (type.equals("system.cpu.usage")) {
			Double valueD = Double.parseDouble(value);
			if (valueD > new Double(0.6)) {
				error = true;
			}
		}
		return error;
	}
	
	private String convert(String type, String value) {
		if (type.equals("jvm.memory.used")) {
			Double valueD = Double.parseDouble(value);
			if (valueD > new Double(1.5E8)) {
				return "high";
			} else {
				return "normal";
			}
		}
		if (type.equals("jvm.memory.max")) {
			Double valueD = Double.parseDouble(value);
			if (valueD < new Double(3E9)) {
				return "low";
			} else {
				return "normal";
			}
		} 
		if (type.equals("system.cpu.usage")) {
			Double valueD = Double.parseDouble(value);
			if (valueD > new Double(0.6)) {
				return "high";
			} else {
				return "normal";
			}
		}
		return "no data";
	}

	private void buildStatus(boolean error, String info) {
		if (!activeHeartBeat) {
			status = "-1"; //Down
		} else if (error) {
			status = "-1"; //Error
		} else if (info.isEmpty() ){
			status = "0"; //No new information 
		} else {
			status = "1"; //Up and running
		}
	}
}
