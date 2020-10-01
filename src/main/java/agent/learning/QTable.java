package agent.learning;

import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import agent.learning.view.QLearningViewControllerManager;


/**
 *	This needs to store a value for each State Action pair, Q(S,A).
 *	The state S is the key. 
 *
 * Limitation: state doesn't encompass state-over-time
 */
public class QTable {

	private static final Logger log = LoggerFactory.getLogger(QTable.class);

	//TODO: review this comment
	//Using HashMaps for now rather than a more efficient data type / using an external memory
	// store as
	// 1. Expecting the number of states to be low
	// 2. Number of actions is 3, so expecting hashmap to contain at most 81 values
	// 3. Prototype ... next stage is to change this to an external database store and plug in 
	// a deep Q learning library, expanding the possible states and actions
	private HashMap<State, HashMap<Action, Double>> qTable;
	
	private double alpha;
	private double gamma;
	private final String agentName;

	public QTable(double alpha, double gamma, String agentName) {
		qTable = new HashMap<>();
		this.alpha = alpha;
		this.gamma = gamma;
		this.agentName = agentName;
	}
	
	/*
	 * New value for taking action A in state S is:
	 * previous value, plus the learning rate alpha times the reward
	 * observed in the new state, S', which is R', plus a 'discount rate' gamma
	 * times that maximum possible value that could be gained from the next
	 * action - the subsequent action taken from the new state S', minus the current
	 * value
	 * Q(S,A) = Q(S,A) + alpha[R' + (gamma * max Q(S',a)) - Q(S,A)]
	 */
	public double add(State S, State S_next, Action A, int R_next) {
		//Get existing value for Q(S,A)
		double existingValue = 0; //Q(S,A)
		HashMap<Action, Double> currentStateActionMapping = qTable.get(S);
		if (currentStateActionMapping == null) {
			currentStateActionMapping = new HashMap<>();
		} else {
			Double value = currentStateActionMapping.get(A);
			if (value != null) {
				existingValue = value.doubleValue();
			}
		}
		
		log.info("Existing value for taking action " + A.getActionEnum().name() + " from state " + S.toString() + " is " + existingValue);
		
		//Get value for the next best possible action
		HashMap<Action, Double> nextStateActionMapping = qTable.get(S_next);
		double highestValue = 0; //max Q'(S,A)
		Action bestAction = null;
		if (nextStateActionMapping != null) {
			for (Action a: nextStateActionMapping.keySet()) {
				Double checkValue = nextStateActionMapping.get(a);
				if (checkValue > highestValue) {
					highestValue = checkValue;
					bestAction = a;
				}
			}
		}

		if (bestAction != null) {
			log.info("Best possible value for an action taken after the next state is " + highestValue + ", for action " + bestAction.toString());
		} else {
			log.info("No data yet on best possible value for action taken after next state");
		}
		
		//[R + (gamma * max Q(S',a)) - Q(S,A)]
		// if the existing value is negative, we don't want to accidentally
		// boost the value of the product with 2 negatives.
		double product = R_next + (gamma * highestValue) - Math.abs(existingValue);
		
		log.info("[R + (gamma * max Q(S',a)) - Q(S,A)] = " + product);
		
		
		//Q(S,A) + alpha[product]
		double finalValue = existingValue + (alpha * product);
		
		log.info("Q(S,A) = " + finalValue);
		
		currentStateActionMapping.put(A, finalValue);
		qTable.put(S, currentStateActionMapping);
		return finalValue;
	}
	
	public Double getValue(State S, Action A) {
		HashMap<Action, Double> currentStateActionMapping = qTable.get(S);
		if (currentStateActionMapping != null) {
			return currentStateActionMapping.get(A);
		}
		return null;
	}

	public Action findBestActionForState(State S) {
		HashMap<Action, Double> nextStateActionMapping = qTable.get(S);
		double highestValue = -10; 
		Action bestAction = null;
		if (nextStateActionMapping != null) {
			for (Action a: nextStateActionMapping.keySet()) {
				Double checkValue = nextStateActionMapping.get(a);
				if (checkValue > highestValue) {
					highestValue = checkValue;
					bestAction = a;
				}
			}
		}
		return bestAction;
		
	}
	
	/*
	 * For UI view
	 */
	public HashMap getQTable() {
		return qTable;
	}
	
	
}
