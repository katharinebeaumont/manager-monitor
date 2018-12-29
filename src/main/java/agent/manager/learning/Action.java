package agent.manager.learning;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import agent.memory.domain.Location;

public class Action {
	
	private ActionEnum actionEnum;
	private Location location;
	
	public Action(ActionEnum actionEnum, Location location) {
		this.actionEnum = actionEnum;
		this.location = location;
	}
	
	public Action(ActionEnum actionEnum) {
		this.actionEnum = actionEnum;
		this.location = null;
	}
	
	@Override
	public String toString() {
		if (location == null) {
			return actionEnum.name();
		}
		return actionEnum.name() + ": " + location.toString();
	}
	
	public ActionEnum getActionEnum() {
		return actionEnum;
	}
	
	public Location getLocation() {
		return location;
	}
	
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Action)) {
			return false;
		}
		
		Action other = (Action)o;
		ActionEnum otherAe = other.getActionEnum();
		Location otherLoc = other.getLocation();
		
		if (!otherAe.equals(actionEnum)) {
			return false;
		}
		
		if (otherLoc != null && location == null) {
			return false;
		} else if (location != null && otherLoc == null) {
			return false;
		}
		
		if (otherLoc == null && location == null) {
			return true;
		}
		
		if (otherLoc.toString().equals(location.toString())) {
			return true;
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		HashCodeBuilder hcb = new HashCodeBuilder(17, 37);
		
		hcb.append(actionEnum.toString());
		if (location != null) {
			hcb.append(location.toString());
		}

		return hcb.toHashCode();
	}

}
