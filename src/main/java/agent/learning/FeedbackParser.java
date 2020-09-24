package agent.learning;

import agent.manager.learning.MonitorStatus;
import agent.memory.domain.Location;

/*
 * TODO: Feedback parser, needs tidyin up
 */
public class FeedbackParser {

	
	public static State parseState(String input, Location loc) throws Exception {
		int indexOfFirstDelim = input.indexOf(":");
		String subStrReward = input.substring(indexOfFirstDelim + 2); //Strip out the quotation mark
		int indexOfSecondDelim = subStrReward.indexOf(";");
		String statusStr = subStrReward.substring(indexOfSecondDelim + 1); //Strip out the reward and the colon
		statusStr = statusStr.replace("}", ""); //Strip out trailing }
		statusStr = statusStr.replace("\"", ""); //Strip out any quotation marks
		statusStr = loc.getPath() + ";" + loc.getPort() + ";" + statusStr; //Add the location
		String[] statusDesc = statusStr.split(";"); //Split on delim
		return new State(statusDesc);
	}
	
	//TODO: 26.1.20 needs configuring as to response. Fix.
	public static EntityStatus parseResponse(String input, String name) {
		String state = input;
		int reward = 0;
		return new MonitorStatus(name, state, reward);
	}
}
