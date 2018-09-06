package agent.manager.learning.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import agent.manager.learning.Action;
import agent.manager.learning.State;

public class QLearningStateViewEntityBuilder {

	public static List<QLearningStateViewEntity> build(State state, HashMap<Action, Double> actionValues) {
		
		List<QLearningStateViewEntity> entites = new ArrayList();
		for (Action a: actionValues.keySet()) {
			entites.add(new QLearningStateViewEntity(state, a.name(), actionValues.get(a).doubleValue()));
		}
		return entites;
	}

}
