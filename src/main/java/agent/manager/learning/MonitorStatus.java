package agent.manager.learning;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import agent.learning.EntityStatus;
import agent.learning.State;
import agent.memory.domain.Location;

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
		setState(stateInfo);
		setReward();
	}
	
	public void setState(String stateInfo) {
		this.state = new State(stateInfo);
	}
	
	/*
	 * WARNING: must be called after set state
	 */
	public void setReward() {
		JSONObject stateDesc = state.getStateDesc();
		if (stateDesc.has("reward")) {
			try {
				this.reward = stateDesc.getInt("reward");
				state.getStateDesc().remove("reward"); //Don't need to store this twice (is stored in Status object)
			} catch (JSONException e) {
				log.error("Could not parse reward from " + state.toString());
			}
		} else {
			log.error("No reward in state " + state.toString() + ", FIXME");
		}
	}
	
	public void setLocation(Location loc) {
		JSONObject stateDesc = state.getStateDesc();
		try {
			stateDesc.put("location", loc.toString());
		} catch (JSONException e) {
			log.error("Failed to add location to State");
		} 
	}

	public MonitorStatus(String name, String response, Location loc) {
		this.agentName = name;
		setState(response);
		setReward();
		setLocation(loc);
	}

	public String agentName() {
		return agentName;
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
