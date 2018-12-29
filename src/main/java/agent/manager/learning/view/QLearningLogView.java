package agent.manager.learning.view;

import java.util.List;

public class QLearningLogView {

	private List<String> logLines;
	
	public QLearningLogView(List<String> logs) {
		this.logLines = logs;
	}
	
	public List<String> getLogLines() {
		return logLines;
	}
}
