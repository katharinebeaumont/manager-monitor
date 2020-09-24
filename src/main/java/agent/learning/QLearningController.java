package agent.learning;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;

/**
 * The Controller processes the feedback from the environment: 
 * for the Manager, this is the feedback from the Monitor
 * for the Monitor, this is it's analysis of/ feedback from the Application
 * 
 * The crucial difference between the Manager's learnings and the Monitor's are that
 * the Monitor is learning from one Application, but the Manager is learning from 
 * multiple Monitors, each with an application.
 * Thus the hashmap qLearningProcesses will have one key for a Monitor, but multiple
 * for the Manager.
 * See agent.manager.learning.QLearningControllerManager
 * and
 * agent.manager.learning.QLearningControllerMonitor
 * for implementations
 * 
 *
 */
@Controller
public abstract class QLearningController<T extends EntityStatus, E extends QLearning> {

	protected HashMap<String, E> qlearningProcesses;

	@Value("${qlearning.lowerRewardThreshold}")
	protected int lowerRewardThreshold;
	
	@Value("${qlearning.gamma}")
	protected double gamma;
	
	@Value("${qlearning.alpha}")
	protected double alpha;
	
	@Value("${qlearning.epsilon}")
	protected double epsilon;
	
	@Value("${qlearning.preferNoAction}")
	protected boolean preferNoAction;
	
	@Value("${qlearning.disable}")
	protected boolean disable;
	
	@Value("${experiment}")
	private int experiment;
	
	public abstract void process(T status);

	public HashMap<String, E> getQLearningProcesses() {
		return qlearningProcesses;
	}
}