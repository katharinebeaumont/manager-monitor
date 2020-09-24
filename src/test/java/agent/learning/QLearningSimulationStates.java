package agent.learning;

import agent.manager.learning.ActionEnum;
import agent.manager.learning.MonitorStatus;

public class QLearningSimulationStates {

    String monitorName;
    int location;
    int jvmMemory;
    int latency;
    int reward;

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
 
        //Else it is DO_NOTHING... in which case:
        if (location == 2) {
            //Make second location have high latency, so low reward
            latency = 1;
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
                jvmMemory = 0;
            }
        }
        if (location == 1) {
            latency = 0;
        }
        
        reward = calculateReward(reward);
        return new MonitorStatus(monitorName, buildStatus(), reward);

    }

    private int calculateReward(int reward) {
    	//Memory of 0 is bad - lower numbers are worse
        if (jvmMemory == 0) {
            reward--;
        }
        //Latency of 1 is bad - higher numbers are worse
        if (latency == 1) {
            reward--;
        }
        return reward;
    }

    private String buildStatus() {
        return location + ":" + jvmMemory + ":" + latency;
    }

}
