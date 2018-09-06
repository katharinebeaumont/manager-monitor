package agent.manager.learning.view;

import java.util.List;

public class QLearningEpisodeView {

	private List<String> episodeSteps;
	
	private double totalValue;
	
	public QLearningEpisodeView(List<String> episodeViewStr) {
		this.episodeSteps = episodeViewStr;
		this.totalValue = parseTotalValue();
	}
	
	private double parseTotalValue() {
		String lastEpisode = episodeSteps.get(episodeSteps.size() - 1);
		int beginIndex = lastEpisode.indexOf("cumulative value:");
		String valueStr = lastEpisode.substring(beginIndex);
		
		valueStr = valueStr.replace("cumulative value:", "");
		valueStr = valueStr.trim();
		double value = 0;
		try {
			value = Double.parseDouble(valueStr);
		} catch (NumberFormatException ex) {
			//Do nothing - not essential if doesn't work.
		}
		return value;
	}

	public List<String> getEpisodeSteps() {
		return episodeSteps;
	}
	
	public double totalValue() {
		return totalValue;
	}
}
