package agent.learning;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.tomcat.util.json.JSONParser;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONString;
import org.json.JSONStringer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * States:
 * - Identical if description is the same
 * - Describe current situation of application or monitor
 */
public class State {

	private static final Logger log = LoggerFactory.getLogger(State.class);

	JSONObject stateDesc;
	
	public State(JSONObject stateDesc) {
		this.stateDesc = stateDesc;
	}
	
	public State(String state) {
		try {
			this.stateDesc = new JSONObject(state);
		} catch (JSONException e) {
			log.error("Failed to create state with " + state);
		}
	}

	public JSONObject getStateDesc() {
		return stateDesc;
	}
	
	/*
	 * Must have same elements of the state desc in the same order
	 * TODO: this should be flexible with order. What if state changes as new value is added?
	 */
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof State)) {
			return false;
		}
		
		State other = (State)o;
		JSONObject otherStateDesc = other.getStateDesc();
		int lengthOther = otherStateDesc.length();
		int lengthThis = stateDesc.length();
		
		if (lengthOther != lengthThis) {
			return false;
		}
		if (otherStateDesc.toString().equals(stateDesc.toString())) {
			return true;
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		HashCodeBuilder hcb = new HashCodeBuilder(17, 37);
		hcb.append(stateDesc.toString());
		return hcb.toHashCode();
	}

	@Override
	public String toString() {
		String retval = "";
		retval += stateDesc.toString();
		return retval.trim();
	}
	
	public static State initialState() {
		try {
			JSONObject initialState = new JSONObject();
			initialState.put("location", "null");
			initialState.put("reward", 0);
			return new State(initialState);
		} catch (JSONException e) {
			log.error("Failed to create inital state");
			return null;
		}
	}
}
