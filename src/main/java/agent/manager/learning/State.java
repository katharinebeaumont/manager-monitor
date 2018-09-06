package agent.manager.learning;

import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * States:
 * - Identical if description is the same
 */
public class State {

	String[] stateDesc;
	
	public State(String[] stateDesc) {
		this.stateDesc = stateDesc;
	}
	
	public String[] getStateDesc() {
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
		String[] otherStateDesc = other.getStateDesc();
		int lengthOther = otherStateDesc.length;
		int lengthThis = stateDesc.length;
		
		if (lengthOther != lengthThis) {
			return false;
		}
		
		boolean sameElements = true;
		for (int i = 0; i<lengthOther; i++) {
			String otherElement = otherStateDesc[i];
			String thisElement = stateDesc[i];
			if (!otherElement.equals(thisElement)) {
				return false;
			}	
		}
		
		return sameElements;
	}
	
	@Override
	public int hashCode() {
		HashCodeBuilder hcb = new HashCodeBuilder(17, 37);
		for (String s: stateDesc) {
			hcb.append(s);
		}
		
		return hcb.toHashCode();
	}

	@Override
	public String toString() {
		String retval = "";
		for (String s: stateDesc) {
			retval += s + " ";
		}
		return retval.trim();
	}
	
	public static State initialState() {
		return new State(new String[] {"initialState"});
	}
}
