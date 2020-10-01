package agent.manager.learning;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import agent.learning.EntityStatus;
import agent.learning.State;

/**
 * The feedback from the environment of the Monitor, sent to the Manager 
 */
public class MonitorStatus extends EntityStatus {
	
	private static final Logger log = LoggerFactory.getLogger(MonitorStatus.class);
	
	private String agentName;
	private State state;
	private int reward = 0;
	
	public MonitorStatus(String agentName, String stateInfo) {
		this.agentName = agentName;
		this.state = new State(stateInfo);
		try {
			this.reward = state.getStateDesc().getInt("reward");
		} catch (JSONException e) {
			log.error("Could not parse reward from " + state.toString());
		}
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
