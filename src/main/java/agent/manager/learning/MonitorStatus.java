package agent.manager.learning;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import agent.learning.Action;
import agent.learning.EntityStatus;
import agent.learning.State;
import agent.memory.domain.Location;

/**
 * The feedback from the environment of the Monitor, sent to the Manager 
 */
public class MonitorStatus extends EntityStatus {
	
	private String agentName;
	private State state;
	private int reward;
	
	public MonitorStatus(String agentName, String state, int reward) {
		this.agentName = agentName;
		this.state = parseState(state);
		this.reward = reward;
	}
	
	private State parseState(String state) {
		//TODO 26.1.20 parse State, splits on :
		return new State(state.split(":"));
	}

	public String agentName() {
		return agentName;
	}
	
	public int reward() {
		return reward;
	}

	@Override
	public State getState() {
		return state;
	}

	@Override
	public int getReward() {
		return reward;
	}
	

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof MonitorStatus)) {
			return false;
		}
		
		MonitorStatus other = (MonitorStatus)o;
		String other_agentName = other.agentName();
		int other_reward = other.getReward();
		State other_state = other.getState();
		
		if (other_agentName.equals(agentName)
			&& other_reward == reward
			&& other_state.equals(state)) {
			return true;
		}
		return false;
	}
	
	@Override
	public String toString() {
		return agentName + ", reward:" + reward + " state:" + state.toString();
	}
	
	@Override
	public int hashCode() {
		HashCodeBuilder hcb = new HashCodeBuilder(7, 327);
		
		hcb.append(state.toString());
		hcb.append(reward);
		hcb.append(agentName);
		
		return hcb.toHashCode();
	}
	

}
