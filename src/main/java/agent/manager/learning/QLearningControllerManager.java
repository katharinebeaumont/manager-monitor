package agent.manager.learning;

import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import agent.learning.Action;
import agent.learning.EntityStatus;
import agent.learning.QLearning;
import agent.learning.QLearningController;
import agent.memory.domain.Location;
import agent.monitor.StatusService;

/**
 * Controls the Q Learning processes of the manager.
 * Stores the learnings per agent name in the hashmap qlearningProcesses
 * Processes the environmental feedback per monitor (MonitorStatus) in 
 * QLearningManager and takes the action prescribed by this.
 * 
 * @author katharine
 *
 */
public class QLearningControllerManager extends QLearningController<MonitorStatus, QLearningManager> {
	
	private static final Logger log = LoggerFactory.getLogger(QLearningControllerManager.class);
	
	@Autowired
	ManagerAgentActions actions;
	
	public void process(MonitorStatus status) {
		if (disable) {
			log.info("Q learning is disabled: not processing " + status);
			return;
		}
		
		if (qlearningProcesses == null) {
			log.info("Q learning process is null. Creating new hashmap.");
			qlearningProcesses = new HashMap();
		}
		
		QLearningManager qLearning;
		
		String agentName = status.agentName();
			
		if (qlearningProcesses.containsKey(agentName)) {
			qLearning = qlearningProcesses.get(agentName);
		} else {
			log.info(agentName + " not registered for learning. Starting now with lower reward threshold " +
					lowerRewardThreshold + ", gamma " + gamma + ", alpha " + alpha + " and epsilon " + epsilon);
			
			qLearning = new QLearningManager(agentName, lowerRewardThreshold, gamma, alpha, epsilon, preferNoAction);
		}
		
		Action actionToTake = qLearning.episodeStep(status);
		qlearningProcesses.put(agentName, qLearning);
		if (actionToTake != null) {
			actions.takeAction(actionToTake, agentName);
		}
		
	}

	//TODO QLearning score: check values
	public void processMonitorDownEvent(String name) {
		String response = "monitor down";
		process(new MonitorStatus(name, response, -10));
	}

}
