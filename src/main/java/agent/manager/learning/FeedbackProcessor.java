package agent.manager.learning;


public class FeedbackProcessor {

	
	public static State processState(String input) throws Exception {
		int indexOfFirstDelim = input.indexOf(":");
		String subStrReward = input.substring(indexOfFirstDelim + 2); //Strip out the quotation mark
		int indexOfSecondDelim = subStrReward.indexOf(";");
		String statusStr = subStrReward.substring(indexOfSecondDelim + 1); //Strip out the reward and the colon
		statusStr = statusStr.replace("}", ""); //Strip out trailing }
		statusStr = statusStr.replace("\"", ""); //Strip out any quotation marks
		String[] statusDesc = statusStr.split(";"); //Split on delim
		return new State(statusDesc);
	}
	
	public static int processReward(String input) throws Exception {
		int indexOfFirstDelim = input.indexOf(":");
		String sub = input.substring(indexOfFirstDelim + 2); //Strip out the quotation mark
		int indexOfSecondDelim = sub.indexOf(";");
		String rewardStr;
		if (indexOfSecondDelim > 0) {
			rewardStr = sub.substring(0, indexOfSecondDelim);
		} else {
			rewardStr = sub;
		}
		rewardStr = rewardStr.replace("}", "");
		rewardStr = rewardStr.replace("\"", "");
		
		int reward = Integer.parseInt(rewardStr);
		return reward;
	}
}
