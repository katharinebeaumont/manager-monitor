package agent.manager.learning.view;

import agent.manager.learning.State;

public class QLearningStateViewEntity {

	private String state;
	private String action;
	private double value;

	public QLearningStateViewEntity(State state, String action, double doubleValue) {
		this.state = state.toString();
		this.action = action;
		this.value = doubleValue;
	}

	public String getState() {
		return state;
	}

	public String getAction() {
		return action;
	}
	
	public double getValue() {
		return value;
	}
}
