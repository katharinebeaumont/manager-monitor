package agent.monitor;


import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.json.JSONArray;
import org.json.JSONObject;

import agent.learning.EntityStatus;
import agent.learning.State;

public class ApplicationStatus extends EntityStatus {
    
	private String applicationName;
	private State state;
	
	/*
	 * Reward is set by Learning Process, unknown on initialisation
	 */
	public ApplicationStatus(String applicationName, String state) {
		this.applicationName = applicationName;
		try {
			this.state = parseState(state);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
    public State getState() {
        return state;
    }
    
    private State parseState(String input) throws Exception {
		JSONObject json = new JSONObject(input);
		String name = (String) json.get("name");
		JSONArray measurementsArray = (JSONArray)json.get("measurements");
		JSONObject item = measurementsArray.getJSONObject(0);
		Double value = (Double) item.get("value");
		String statusStr = "{name:" + name + ";value:" + value + "}";
	    return new State(statusStr);
	}
    

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof ApplicationStatus)) {
			return false;
		}
		
		ApplicationStatus other = (ApplicationStatus)o;
		String other_agentName = other.applicationName();
		int other_reward = other.getReward();
		State other_state = other.getState();
		
		if (other_agentName.equals(applicationName)
			&& other_state.equals(state)) {
			return true;
		}
		return false;
	}
	
	private String applicationName() {
		return applicationName;
	}

	@Override
	public String toString() {
		return applicationName + ", state:" + state.toString();
	}
	
	@Override
	public int hashCode() {
		HashCodeBuilder hcb = new HashCodeBuilder(3, 3987);
		
		hcb.append(state.toString());
		hcb.append(applicationName);
		
		return hcb.toHashCode();
	}

	@Override
	public int getReward() {
		// TODO clean up required. EntityStatus requiring reward doesn't work...
		//Unless monitor does Q Learning too. Which shouldn't have to
		return 0;
	}
}
