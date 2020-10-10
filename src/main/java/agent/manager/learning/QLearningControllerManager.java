package agent.manager.learning;

import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import agent.learning.Action;
import agent.learning.LearningController;
import agent.memory.DBInterface;
import agent.memory.domain.Location;

/**
 * Controls the Q Learning processes of the manager.
 * Stores the learnings per agent name in the hashmap qlearningProcesses
 * Processes the environmental feedback per monitor (MonitorStatus) in 
 * QLearningManager and takes the action prescribed by this.
 * 
 * @author katharine
 *
 */
@Component
public class QLearningControllerManager extends LearningController<MonitorStatus, QLearningManager> {
	
	private static final Logger log = LoggerFactory.getLogger(QLearningControllerManager.class);
	
	@Autowired
	DBInterface dbInterface;
	
	@Autowired
	ManagerAgentActions actions;
	
	public boolean process(MonitorStatus status) {
		if (disable) {
			log.info("Q learning is disabled: not processing " + status);
			return false;
		}
		
		if (learningProcesses == null) {
			log.info("Q learning process is null. Creating new hashmap.");
			learningProcesses = new HashMap<String, QLearningManager>();
		}
		QLearningManager qLearning;
		String agentName = status.agentName();
		if (learningProcesses.containsKey(agentName)) {
			qLearning = learningProcesses.get(agentName);
		} else {
			log.info(agentName + " not registered for learning. Starting now with lower reward threshold " +
					lowerRewardThreshold + ", gamma " + gamma + ", alpha " + alpha + " and epsilon " + epsilon);
			
			qLearning = new QLearningManager(dbInterface, agentName, lowerRewardThreshold, gamma, alpha, epsilon, preferNoAction);
		}
		
		Action actionToTake = qLearning.episodeStep(status);
		learningProcesses.put(agentName, qLearning);
		if (actionToTake != null) {
			actions.takeAction(actionToTake, agentName);
			if (actionToTake.getActionEnum().equals(ActionEnum.MOVE)) {
				log.info("Moving " + agentName + " and monitor to " + actionToTake.getLocation().toString());
				return true;
			}
		}
		return false;
		
	}

	//TODO QLearning score: check values
	public void processMonitorDownEvent(String name, Location loc) {
		String response = "{\"system\":\"monitor down\",\"reward\":-5}";
		process(new MonitorStatus(name, response, loc));
	}

}
