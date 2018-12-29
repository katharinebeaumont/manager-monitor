package agent.manager.learning;

import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;

import agent.manager.ManagerAgentActions;
import agent.memory.domain.Location;


@Controller
public class QLearningController {

	private HashMap<String, QLearning> qlearningProcesses;

	private static final Logger log = LoggerFactory.getLogger(QLearningController.class);
	
	@Autowired
	private ManagerAgentActions actions;
	
	@Value("${qlearning.lowerRewardThreshold}")
	private int lowerRewardThreshold;
	
	@Value("${qlearning.gamma}")
	private double gamma;
	
	@Value("${qlearning.alpha}")
	private double alpha;
	
	@Value("${qlearning.epsilon}")
	private double epsilon;
	
	@Value("${qlearning.preferNoAction}")
	private boolean preferNoAction;
	
	@Value("${qlearning.disable}")
	private boolean disable;
	
	@Value("${experiment}")
	private int experiment;
	
	public void process(String agentName, Location loc, String response) {
		if (disable) {
			log.info("Q learning is disabled: not processing " + response);
			return;
		}
		
		if (qlearningProcesses == null) {
			qlearningProcesses = new HashMap();
		}

		QLearning qLearning;
		if (qlearningProcesses.containsKey(agentName)) {
			qLearning = qlearningProcesses.get(agentName);
		} else {
			log.info(agentName + " not registered for learning. Starting now with lower reward threshold " +
					lowerRewardThreshold + ", gamma " + gamma + ", alpha " + alpha + " and epsilon " + epsilon);
			
			qLearning = new QLearning(agentName, lowerRewardThreshold, gamma, alpha, epsilon, preferNoAction);
		}
		
		Action actionToTake = qLearning.episodeStep(response, actions.getAvailableLocations());
		qlearningProcesses.put(agentName, qLearning);
		if (actionToTake != null) {
			takeAction(actionToTake, agentName);
		}
	}
	
	private void takeAction(Action actionToTake, String agentName) {
		log.info(agentName + " taking action " + actionToTake.toString());
		ActionEnum ae = actionToTake.getActionEnum();
		if (ae.equals(ActionEnum.DO_NOTHING)) {
			//Do nothing
		} else if (ae.equals(ActionEnum.DUPLICATE_AND_DEPLOY) && experiment != 2) {
			actions.duplicatedAndDeploy(agentName, actionToTake.getLocation());
		} else if (ae.equals(ActionEnum.SHUTDOWN_AND_REDEPLOY)) {
			actions.shutdownAndRedeploy(agentName, actionToTake.getLocation());
		} else {
			throw new RuntimeException("Action " + actionToTake + " not implemented.");
		}
	}

	public HashMap<String, QLearning> getQLearningProcesses() {
		return qlearningProcesses;
	}

	public void processMonitorDownEvent(String name, Location loc) {
		String response = "{\"response\":\"-25;monitor-is-down\"}";
		process(name, loc, response);
	}
}