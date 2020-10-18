package agent.learning;

import org.json.JSONException;
import org.json.JSONObject;

import agent.manager.learning.ActionEnum;
import agent.manager.learning.MonitorStatus;

public class QLearningSimulationStates {

    String monitorName;
    int location; //1 or 2
    String jvmMemory; //normal or high
    String cpuUsage; //normal or high
    int test = 1;
    
    /*
     * Initialise the location to 1
     */
    public QLearningSimulationStates(String monitorName) {
        this.monitorName = monitorName;
         this.location = 1;
        this.jvmMemory = "normal";
    }
    
    public QLearningSimulationStates(String monitorName, int test) {
        this.monitorName = monitorName;
        this.location = 1;
        this.jvmMemory = "normal";
        this.test = test;
    }

    public MonitorStatus getState(Action a) {
        int reward = 0;
        if (a.getActionEnum() == ActionEnum.MOVE) {
            String path = a.getLocation().getPath();
            location = Integer.parseInt(path);
        }
        
        if (test == 1) {
        	return test1(reward);
        } else if (test == 2) {
        	return test2(reward);
        } else {
        	return test3(reward);
        }
    }

	private MonitorStatus test1(int reward) {
		//Alternate between JVM memory and CPU usage
        int heads_or_tails = (int) (Math.random() * 2);
        if (heads_or_tails == 0) {
        	jvmMemory = "normal";
        	if (location == 2) {
                double randomChoice = (Math.random() * 5);
                if (randomChoice > 2.5) {
                    jvmMemory = "normal";
                } else {
                	jvmMemory = "high";
                	reward--;
                }
            }
        	return new MonitorStatus(monitorName, buildStatusJVM(reward));
        }
        else {
        	cpuUsage = "normal";
        	if (location == 2) {
        		double randomChoice = (Math.random() * 5);
                if (randomChoice > 2.5) {
                	cpuUsage = "normal";
                } else {
	        		cpuUsage = "high";
	        		reward--;
                }
        	}
        	return new MonitorStatus(monitorName, buildStatusLatency(reward));
        }
	}

	private MonitorStatus test2(int reward) {
		//Alternate between JVM memory and CPU usage
        int heads_or_tails = (int) (Math.random() * 2);
        if (heads_or_tails == 0) {
        	jvmMemory = "high";
        	if (location == 1) {
                double randomChoice = (Math.random() * 5);
                if (randomChoice > 2.5) {
                    jvmMemory = "normal";
                } else {
                	jvmMemory = "high";
                }
            }
        	if (jvmMemory.equals("high")) {
        		reward--;
        	}
        	return new MonitorStatus(monitorName, buildStatusJVM(reward));
        }
        else {
        	cpuUsage = "high";
        	if (location == 1) {
        		double randomChoice = (Math.random() * 5);
                if (randomChoice > 2.5) {
                	cpuUsage = "normal";
                } else {
	        		cpuUsage = "high";
	        	}
        	}
        	if (cpuUsage.equals("high")) {
        		reward--;
        	}
        	return new MonitorStatus(monitorName, buildStatusLatency(reward));
        }
	}
	
	private MonitorStatus test3(int reward) {
		//Alternate between JVM memory and CPU usage
        int heads_or_tails = (int) (Math.random() * 2);
        if (heads_or_tails == 0) {
    	    double randomChoice = (Math.random() * 5);
            if (randomChoice > 2.5) {
                jvmMemory = "normal";
            } else {
            	jvmMemory = "high";
            	reward--;
            }
        	
        	return new MonitorStatus(monitorName, buildStatusJVM(reward));
        }
        else {
        	double randomChoice = (Math.random() * 5);
            if (randomChoice > 2.5) {
            	cpuUsage = "normal";
            } else {
        		cpuUsage = "high";
        		reward--;
            }
        	return new MonitorStatus(monitorName, buildStatusLatency(reward));
        }
	}
	
    private String buildStatusLatency(int reward) {
    	JSONObject status = new JSONObject();
    	try {
			status.put("location", location);
	    	status.put("cpuUsage", cpuUsage);
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
