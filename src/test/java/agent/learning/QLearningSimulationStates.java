package agent.learning;

import org.json.JSONException;
import org.json.JSONObject;

import agent.manager.learning.ActionEnum;
import agent.manager.learning.MonitorStatus;

public class QLearningSimulationStates {

    String monitorName;
    int location;
    int jvmMemory;
    int latency;

    /* Reminder: (STATE_ACTION_GUIDE.md)
    State

    This comprises of

        Monitor identifier
        Location: which machine and port the monitor is located on
       [ Is Duplicate flag: are there multiple monitors observing duplicate microservices - for later ]
        Microservice internals:
        JVM memory
        Network latency

     */

    public QLearningSimulationStates(String monitorName) {
        this.monitorName = monitorName;
        this.location = 1;
    }

    public MonitorStatus getState(Action a) {
        int reward = 0;
        if (a.getActionEnum() == ActionEnum.MOVE) {
            String path = a.getLocation().getPath();
            location = Integer.parseInt(path);
        }
        
        //Alternate between JVM memory and latency
        int heads_or_tails = (int) (Math.random() * 2);
        if (heads_or_tails == 0) {
        	if (location == 2) {
                //Only changing JVM memory when in location 2
                // means that moving from location 1 (as an action)
                // always takes you to a worse or equally bad place
                // however if you don't move from 1 0 0, ... this is bad?
                // should get equal options with move and stay put?
                // No - better to move because getting into 2 means
                // potentially getting back to 1 1 0. Demos more complex learning
                double randomChoice = (Math.random() * 5);
                if (randomChoice > 2) {
                    jvmMemory = 1;
                }
                if (randomChoice <= 2) {
                	//Memory of 0 is bad - lower numbers are worse
                    jvmMemory = 0;
                    reward--;
                }
            }
        	return new MonitorStatus(monitorName, buildStatusJVM(reward));
        }
        else {
        	if (location == 2) {
        		//Make second location have high latency, so low reward
        		//Latency of 1 is bad - higher numbers are worse
                latency = 1;
        		reward--;
        	}
        	if (location == 1) {
                latency = 0;
            }
        	return new MonitorStatus(monitorName, buildStatusLatency(reward));
        }

    }

    private String buildStatusLatency(int reward) {
    	JSONObject status = new JSONObject();
    	try {
			status.put("location", location);
	    	status.put("latency", latency);
	    	status.put("reward", reward);
		} catch (JSONException e) {
			e.printStackTrace();
		}
    	
    	return status.toString();
	}

	private String buildStatusJVM(int reward) {
		JSONObject status = new JSONObject();
    	try {
			status.put("location", location);
			status.put("jvmMemory", jvmMemory);
	    	status.put("reward", reward);
		} catch (JSONException e) {
			e.printStackTrace();
		}
    	
    	return status.toString();
	}

}
