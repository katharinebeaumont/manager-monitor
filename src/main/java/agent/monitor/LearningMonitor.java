package agent.monitor;


import agent.learning.Action;
import agent.learning.QLearning;
import agent.learning.State;


/**
 * TODO: QLearningMonitor needs writing
 * 
 */
public class LearningMonitor extends QLearning<ApplicationStatus> {


	public LearningMonitor(String agentName, int lowerRewardThreshold, double gamma, double alpha, double epsilon) {
		super(agentName, lowerRewardThreshold, gamma, alpha, epsilon);
	}

	@Override
	public Action pickActionAtRandom(State S_next) {
		return null;
	}

	@Override
	public Action pickBestAction(State S_next) {
		return null;
	}
}
