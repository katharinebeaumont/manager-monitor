package agent.manager.learning;

import java.util.HashMap;

/**
 * One per monitoring agent
 * TODO: change so it is one per application and location?
 *	This needs to store a value for each State Action pair, Q(S,A).
 *	The state S is the key. 
 *
 * Limitation: state doesn't encompass state-over-time
 */
public class QTable {

	//Using HashMaps for now rather than a more efficient data type / using an external memory
	// store as
	// 1. Expecting the number of states to be low (3^3)
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
	 * Q(S,A) + alpha[R + (gamma * max Q(S',a) - Q(S,A)]
	 */
	public double add(State S, State S_next, Action A, int R) {
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
		
		//Get best possible value for the next action
		HashMap<Action, Double> nextStateActionMapping = qTable.get(S_next);
		double highestValue = 0; //max Q'(S,A)
		if (nextStateActionMapping != null) {
			for (Action a: nextStateActionMapping.keySet()) {
				Double checkValue = nextStateActionMapping.get(a);
				if (checkValue > highestValue) {
					highestValue = checkValue;
				}
			}
		}
		
		//[R + (gamma * max Q(S',a) - Q(S,A)]
		double product = R + (gamma * highestValue) - existingValue;
		
		//Q(S,A) + alpha[product]
		double finalValue = existingValue + (alpha * product);
		
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
		double highestValue = Double.MIN_NORMAL; 
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
