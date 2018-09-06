package agent.manager.learning.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import agent.manager.learning.QLearning;
import agent.manager.learning.QLearningController;
import agent.manager.learning.QTable;
import agent.manager.learning.State;

/*
 * Shows learnings of the manager per agent.
 */
@Controller
public class QLearningViewController {

	private static final Logger log = LoggerFactory.getLogger(QLearningViewController.class);
	
	@Autowired
	private QLearningController qLearningController;
	
	@GetMapping("/agents")
	public String getAgentNames(Model model) {
		log.info("Creating view");
		//Drop down option to select agent by name
		HashMap<String, QLearning> qTables = qLearningController.getQLearningProcesses();
		List<QLearningAgentViewEntity> agentNames = new ArrayList(); 
		
		if (qTables != null && !qTables.isEmpty()) {
			
			for (String agentName: qTables.keySet()) {
				QLearningAgentViewEntity agent = new QLearningAgentViewEntity(agentName);
				agentNames.add(agent);
			}
			
		} else {
			log.info("Q tables is null");
		}
		
		model.addAttribute("agentNames", agentNames);
		
		return "agents";
	}
	
	@GetMapping("/agent/learning/{agentName}")
	public String showLearningsForAgent(@PathVariable("agentName") String agentName, Model model) {
		log.info("Creating view for " + agentName);
		//Drop down option to select agent by name
		HashMap<String, QLearning> qTables = qLearningController.getQLearningProcesses();
		List<QLearningStateViewEntity> stateViews = new ArrayList<>();
		int episodeCount = 0;
		double current_value = 0;
		if (qTables != null && !qTables.isEmpty()) {
			QLearning learning = qTables.get(agentName);
			if (learning != null) {
				QTable table = learning.getQTable();
				
				HashMap<State, HashMap> map = table.getQTable();
				log.debug("Q table for " + agentName + " is size " + map.size());
				for (State s: map.keySet()) {
					log.debug("Adding actionValues for " + s.toString());
					List<QLearningStateViewEntity> views = QLearningStateViewEntityBuilder.build(s, map.get(s));
					log.debug("ActionValues size is " +  map.get(s).size());
					stateViews.addAll(views);
				}
				current_value = learning.getCurrentValue();
				List<List<String>> episodeSteps = learning.getEpisodeSteps();
				episodeCount = episodeSteps.size();
				
			} else {
				log.info("Q table is null for agent " + agentName);
			}
		} else {
			log.info("No information on learnings yet");
		}
		
		model.addAttribute("agentName", agentName);
		model.addAttribute("episodeCount", episodeCount);
		model.addAttribute("episodes", createEpisodeDropDown(episodeCount));
		model.addAttribute("currentEpisodeValue", current_value);
		model.addAttribute("stateViews", stateViews);
		
		//Visualise Q Table
		return "agent";
	}
	
	@GetMapping("/agent/episode/{agentName}")
	public String showLearningsForAgentAndEpisode(@PathVariable("agentName") String agentName,
			@RequestParam("episode") String episodeStr, Model model) {
		
		int episode = parseEpisode(episodeStr);
		log.info("Creating episode view for " + agentName + ", episode " + episode);
		int stepCount = 0;
		
		HashMap<String, QLearning> qTables = qLearningController.getQLearningProcesses();
		if (qTables != null && !qTables.isEmpty()) {
			QLearning learning = qTables.get(agentName);
			if (learning != null) {
				List<List<String>> episodeSteps = learning.getEpisodeSteps();
				List<String> episodeViewStr = episodeSteps.get(episode);
				stepCount = episodeViewStr.size();
				QLearningEpisodeView episodeView = new QLearningEpisodeView(episodeViewStr);
				model.addAttribute("episodeView", episodeView);
			} else {
				log.info("Q table is null for agent " + agentName);
			}
		} else {
			log.info("No information on learnings yet");
		}
		
		model.addAttribute("agentName", agentName);
		model.addAttribute("stepCount", stepCount);
		model.addAttribute("episodeNo", episode);
		
		return "agentepisode";
		
		
	}
	
	
	private int parseEpisode(String episodeStr) {
		episodeStr = episodeStr.replace("Episode", "");
		episodeStr = episodeStr.trim();
		int value = 0;
		try {
			value = Integer.parseInt(episodeStr);
		} catch (NumberFormatException ex) {
			log.info("Could not parse int from " + episodeStr);
		}
		return value;
	}

	private List<String> createEpisodeDropDown(int episodeCount) {
		List<String> dropDownOptions = new ArrayList<>();
		for (int i=0; i<episodeCount; i++) {
			dropDownOptions.add("Episode " + i);
		}
		return dropDownOptions;
	}

}
