package agent.manager.learning;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import agent.memory.domain.Location;


/**
 * One per agent
 * Episode ends when reward reaches a minimum value (failure threshold)
 * rather than a maximal value
 * 
 */
public class QLearning {

	private static final Logger log = LoggerFactory.getLogger(QLearning.class);
	
	private QTable qTable; //The table of values for each state action pair
	private int lowerRewardThreshold; //The threshold at which an episode is considered to be concluded: here it is a lower
									 // threshold as we are trying to avoid long down time, so it is a negative scale
	private double epsilon; //The probability at which the agent will explore
	private double totalValue; //Keep track of the value accumulated per episode
	private boolean preferNoAction; //A policy that, combined with epsilon greedy, ensures the agent picks to do nothing more often
								   // than other actions when randomly picking the next action.
	
	// The last action A gave us the state S
	// This is initialised to DO_NOTHING
	private Action A = new Action(ActionEnum.DO_NOTHING);
	// The initial state is not stored in the Q table
	private State S = State.initialState();
	
	private List<List<String>> episodeSteps;
	
	public QLearning(String agentName, int lowerRewardThreshold, double gamma, double alpha, double epsilon, boolean preferNoAction) {
		this.lowerRewardThreshold = lowerRewardThreshold;
		this.epsilon = epsilon;
		this.totalValue = 0;
		this.qTable = new QTable(alpha, gamma, agentName);
		this.preferNoAction = preferNoAction;
		this.episodeSteps = new ArrayList<>();
	}
	
	public void reset() {
		A = new Action(ActionEnum.DO_NOTHING);
		S = State.initialState();
		totalValue = 0;
		episodeSteps.add(new ArrayList<String>());
	}
	
	private void addToSteps() {
		String step = "State: " + S + ", Action: " + A + ", cumulative value: " + totalValue;
		List<String> steps = new ArrayList<String>();
		if (episodeSteps.size() > 0) {
			steps = episodeSteps.get(episodeSteps.size()-1);
		} else {
			episodeSteps.add(steps);
		}
		steps.add(step);
	}
	
	/*
	 * Q(S,A) <- Q(S,A) + alpha[R + (gamma * max Q(S',a) - Q(S,A)]
	 * 
	 * A: in episodeStep(S), we have just taken action A to observe R, S' (S_next)
	 * 
	 * @param feedback: this is the state we have just been given by the monitoring agent 
	 * from taking action A (S') and the reward R
	 * 
	 * As we take an episode step, we have just taken action A. We use S_next and R to 
	 * update the Q table in processReward()
	 * 
	 * Then we choose A from S using the policy
	 * We return the Action to the QLearningController, which is executed.
	 * 
	 * At each step, we need to feed in the available locations as this will change
	 * over time
	 */
	public Action episodeStep(String feedback, List<Location> availableLocations) {
		
		State S_next = processState(feedback);
		//Record reward from last state
		// Retrieve R from S' and update the Q table
		int R = processReward(feedback);
		
		//We don't always get feedback from the envirnonment, which is signified by a reward of 0.
		if (R == 0) {
			return new Action(ActionEnum.DO_NOTHING);
		}
		
		if (!S.equals(State.initialState())) {
			//We don't count the first state
			totalValue += storeReward(R, S_next); // This is where Q(S,A) <- Q(S,A) + alpha[R + (gamma * max Q(S',a) - Q(S,A)] happens
			addToSteps();
		}
		
		// Have we reached a failure threshold for the episode?
		if (!continueEpisode()) {
			reset();
			return null;
		}
		
		// Select A' from S'
		Action A_next = chooseNextAction(S_next, availableLocations);
		
		//Update the values
		A = A_next;
		S = S_next;
		
		//Take the next Action
		return A_next;
	}
	
	private State processState(String feedback) {
		try {
			return FeedbackProcessor.processState(feedback);
		} catch (Exception e) {
			log.error("Error processing State for feedback: " + feedback);
		}
		return null;
	}

	public int processReward(String feedback) {
		try {
			return FeedbackProcessor.processReward(feedback);
		} catch (Exception e) {
			log.error("Error processing Reward for feedback: " + feedback);
		}
		return 0;
	}
	
	private double storeReward(int R, State S_next) {
		return qTable.add(S, S_next, A, R);
	}
	
	private boolean continueEpisode() {
		if (totalValue <= lowerRewardThreshold) {
			return false;
		}
		return true;
	}

	private Action chooseNextAction(State S_next, List<Location> availableLocations) {
		if (Math.random() < epsilon) {
			return pickActionAtRandom(availableLocations);
		}
		return pickBestAction(S_next, availableLocations);
	}

	private Action pickActionAtRandom(List<Location> availableLocations) {
		ActionEnum[] actions = ActionEnum.values();
		int randomAction = (int) (Math.random() * actions.length);
		ActionEnum chosenAction = actions[randomAction];
		
		if (preferNoAction && !chosenAction.equals(ActionEnum.DO_NOTHING)) {
			//Weight do nothing 10x higher than the alternative action
			int existingActionOrNone = (int) (Math.random() * 10);
			if (existingActionOrNone > 0) {
				chosenAction = ActionEnum.DO_NOTHING;
				return new Action(chosenAction);
			}
		}
		if (chosenAction.equals(ActionEnum.DO_NOTHING)) {
			return new Action(chosenAction);
		}
		
		int randomLocation = (int) (Math.random() * availableLocations.size());
		Location loc = availableLocations.get(randomLocation);
		
		return new Action(chosenAction, loc);
	}
	

	private Action pickBestAction(State S_next, List<Location> availableLocations) {
		//If there are two actions that are equally good, this picks the last one to be added
		// to the hashmap.
		// If there is none, fallback on random.
		Action A_next = qTable.findBestActionForState(S_next);
		if (A_next == null) {
			return pickActionAtRandom(availableLocations);
		}
		return A_next;
	}
	
	/*
	 * For UI
	 */
	public QTable getQTable() {
		return qTable;
	}
	
	public double getCurrentValue() {
		return totalValue;
	}
	
	public List<List<String>> getEpisodeSteps() {
		return episodeSteps;
	}
}
