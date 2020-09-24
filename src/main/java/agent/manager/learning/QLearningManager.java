package agent.manager.learning;

import agent.memory.DBInterface;
import agent.memory.domain.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import agent.learning.Action;
import agent.learning.QLearning;
import agent.learning.State;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

public class QLearningManager extends QLearning<MonitorStatus> {

	@Autowired
	DBInterface dbInterface;

	private static final Logger log = LoggerFactory.getLogger(QLearningManager.class);
	List<Action> actions = new ArrayList<Action>();

	private boolean preferNoAction;
	
	public QLearningManager(String agentName, int lowerRewardThreshold, double gamma, double alpha, double epsilon, boolean preferNoAction) {
		super(agentName, lowerRewardThreshold, gamma, alpha, epsilon);
		this.preferNoAction = preferNoAction;
		actions.add(new Action(ActionEnum.DO_NOTHING));
	}

	public List<Action> updateAvailableLocations(String currentLocation) {
		actions = new ArrayList<Action>();
		actions.add(new Action(ActionEnum.DO_NOTHING));
		List<Location> locations = dbInterface.getAvailableLocations();
		for (Location l: locations) {
			if (!l.toString().equals(currentLocation)) {
				actions.add(new Action(ActionEnum.MOVE, l));
			}
		}
		return actions;
	}

	/*
	 * Just picks the actions. Any other decisions (e.g. location) happens somewhere else.
	 */
	public Action pickActionAtRandom(State S_next) {
		//TODO this needs to be more robust
		String currentLocation = S_next.getStateDesc()[0];
		//Returning the array despite class scope due to test
		// where need to mock the call to DBInterface
		actions = updateAvailableLocations(currentLocation);

		int weighting = 3;
		int existingActionOrNone = (int) (Math.random() * weighting);

		//Weight do nothing higher than the alternative actions
		if (preferNoAction && existingActionOrNone > 0) {
			log.info("Weighted choice :" + ActionEnum.DO_NOTHING);
			return new Action(ActionEnum.DO_NOTHING);
		}

		int randomAction = (int) (Math.random() * actions.size());
		Action chosenAction = actions.get(randomAction); //Can't choose to move to itself
		
		return chosenAction;
	}

	public Action pickBestAction(State S_next) {
		//TODO this needs to be more robust
		String currentLocation = S_next.getStateDesc()[0];
		//Returning the array despite class scope due to test
		// where need to mock the call to DBInterface
		actions = updateAvailableLocations(currentLocation);
	
		//If there are two actions that are equally good, this picks the last one to be added
		// to the hashmap.
		// If there is none, fallback on random.
		Action A_next = qTable.findBestActionForState(S_next);
		if (A_next == null || !actions.contains(A_next)) {
			log.debug("It is not possible to take action " + A_next + ", so going random");
			//Location isn't available, fall back on random
			return pickActionAtRandom(S_next);
		}
		
		return A_next;
	}
}

