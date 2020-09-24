package agent.learning;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import agent.manager.learning.ActionEnum;


/**
 * TODO: explain class
 */
public abstract class QLearning<T extends EntityStatus> {

	private static final Logger log = LoggerFactory.getLogger(QLearning.class);

	protected QTable qTable; //The table of values for each state action pair
	protected int lowerRewardThreshold; //The threshold at which an episode is considered to be concluded: here it is a lower
									 // threshold as we are trying to avoid long down time, so it is a negative scale
	protected double epsilon; //The probability at which the agent will explore
	protected double currentTotalValue; //Keep track of the value accumulated per episode
	
	// The last action A gave us the state S
	// This is initialised to DO_NOTHING
	protected Action A_previous = new Action(ActionEnum.DO_NOTHING);
	// The initial state is not stored in the Q table
	protected State S_previous = State.initialState();
	
	protected List<List<String>> episodeSteps;
	
	public QLearning(String agentName, int lowerRewardThreshold, double gamma, double alpha, double epsilon) {
		this.lowerRewardThreshold = lowerRewardThreshold;
		this.epsilon = epsilon;
		this.currentTotalValue = 0;
		this.qTable = new QTable(alpha, gamma, agentName);
		this.episodeSteps = new ArrayList<>();
	}

	/*
	 * Starts next episode.
	 */
	public void startNewEpisode() {
		A_previous = new Action(ActionEnum.DO_NOTHING);
		S_previous = State.initialState();
		currentTotalValue = 0;
		episodeSteps.add(new ArrayList<String>());
	}

	/*
	 * This is always looking behind: the State, the Action chosen from that
	 * state, and the reward that was given.
	 */
	private void addToSteps(State S, Action A, int R) {
		String step = "State: " + S + ";Action: " + A + ";Reward: " + R;
		List<String> steps = new ArrayList<String>();
		//Get the latest episode
		if (episodeSteps.size() > 0) {
			steps = episodeSteps.get(episodeSteps.size()-1);
		} else {
			episodeSteps.add(steps);
		}
		steps.add(step);
	}
	
	/*
	 * episodeStep(S), we have just taken action A to observe R, S' (S_next)
	 *
	 * We can then get an updated value for what happens being in the state, S, and from there taking Action A
	 * Q(S,A) <- Q(S,A) + alpha[R + (gamma * max Q(S',a) - Q(S,A)]
	 * i.e. the qTable and episode steps are always one behind, because we are analysing the effects of the previous
	 * state-action occurance, to find out what the best action to take is in each state.
	 * Here this translates as Q(S_previous, A_previous), based on what R happened in S
	 *
	 * @param status: this is the state we have just been given by the environmental feedback (from agent or application)
	 * from taking action A in state S, to get S' and the reward R
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
	public Action episodeStep(T status) {
		//Retire needs to signify end of learning and monitoring.

		State S = status.getState();

		//Record reward from last state
		// Retrieve R from S' and update the Q table
		int R = status.getReward();
		log.info("State was " + S_previous + " but after Action: " + A_previous + ", is now " + S.toString() + ", and the reward is: " + R);

		//Q(S_previous, A_previous)
		//Don't store the reward for the initial state
		if (!S_previous.equals(State.initialState())) {
			currentTotalValue += storeReward(R, S); // This is where Q(S,A) <- Q(S,A) + alpha[R + (gamma * max Q(S',a) - Q(S,A)] happens
		}

		// Select A' from S'
		Action A = chooseNextAction(S);
		
		// Have we reached a failure threshold for the episode
		// or - are we shutting down and redeploying?
		if (!continueEpisode()) {
			startNewEpisode();
			return null;
		}
		//Store the State: Action pair, plus the total value
		addToSteps(S_previous, A_previous, R); //This is always looking at the previous state and action.
		// The current one (new A and S) are not added yet, because we don't know the environmental feedback from the action
		// until it is taken.

		//Update the values
		A_previous = A;
		S_previous = S;

		//Take the next Action
		return A;
	}

	// Q(S_previous, A_previous) = Q(S_previous, A_previous) + alpha[R' + (gamma * max Q(S,a) - Q(S_previous, A_previous)]
	private double storeReward(int R, State S) {
		return qTable.add(S_previous, S, A_previous, R);
	}
	
	private boolean continueEpisode() {
		if (currentTotalValue <= lowerRewardThreshold) {
			return false;
		}
		return true;
	}

	private Action chooseNextAction(State S_next) {
		if (Math.random() < epsilon) {
			return pickActionAtRandom(S_next);
		}
		return pickBestAction(S_next);
	}

	/*
	 * Methods that need implementing by children of QLearning
	 */
	public abstract Action pickActionAtRandom(State S_next);
	
	public abstract Action pickBestAction(State S_next);
	
	/*
	 * For UI
	 */
	public QTable getQTable() {
		return qTable;
	}
	
	public double getCurrentValue() {
		return currentTotalValue;
	}

	public List<List<String>> getEpisodeSteps() {
		return episodeSteps;
	}

	/*
	 * For Testing
	 */
	public Action getPreviousAction() {
		return A_previous;
	}
	public State getPreviousState() {
		return S_previous;
	}
}
