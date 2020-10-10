package agent.monitor;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import agent.learning.LearningController;
import agent.learning.State;
import agent.monitor.metrics.MonitorEventStore;

/*
 * TODO: Describe learning process
 * Bit hacky, but asssign a reward to application feedback
 * In place of a rules engine, hardcoded.
 */
@Component
public class LearningControllerMonitor  extends LearningController<ApplicationStatus, LearningMonitor> {

	private static final Logger log = LoggerFactory.getLogger(LearningControllerMonitor.class);
	
	@Autowired
	private MonitorEventStore eventStore;
	
	double jvmMax = 1;
	
    @Override
    public boolean process(ApplicationStatus appStatus) {
    	JSONObject event = new JSONObject();
    	
    	State s = appStatus.getState();
    	JSONObject stateDesc = s.getStateDesc();
    	try {
			String name = stateDesc.getString("name");
			double value = stateDesc.getDouble("value");
			//Don't store this, just use it to judge how good jvm memory is
			if (name.equals("jvm.memory.max")) {
				jvmMax = value;
				return true;
			}
			event = convert(name, value, event);
		} catch (JSONException e) {
			log.error("could not parse state descriptors for " + s.toString());
		}
    	
    	eventStore.add(event);   
    	return true;
    }

	public void processLostContact() {
		JSONObject event = new JSONObject();
		try {
			event.put("name", "system is down");
			event.put("reward", -5);
		} catch (JSONException e) {
			log.error("could not create state for loss of contact");
		}
		
		eventStore.add(event);
	}
	
	/*
	 * also adds timestamp
	 */
	private JSONObject convert(String name, double value, JSONObject event) throws JSONException {
		int reward = 0;
		long timestamp = System.currentTimeMillis();
		event.put("timestamp", timestamp);
		if (name.equals("jvm.memory.used")) {
			double percentageJvmUsed = value/jvmMax;
			if (percentageJvmUsed > new Double(0.8)) {
				event.put(name, "high");
				reward = -1;
			} else {
				event.put(name, "normal");
			}
		}
		if (name.equals("system.cpu.usage")) {
			if (value > new Double(0.6)) {
				event.put(name, "high");
				reward = -1;
			} else {
				event.put(name, "normal");
			}
		}
		event.put("reward", reward);
		return event;
	}
}
