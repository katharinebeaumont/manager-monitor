package agent.learning.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import agent.learning.Action;
import agent.learning.State;

public class QLearningStateViewEntityBuilder {

	public static List<QLearningStateViewEntity> build(State state, HashMap<Action, Double> actionValues) {
		
		List<QLearningStateViewEntity> entites = new ArrayList();
		for (Action a: actionValues.keySet()) {
			entites.add(new QLearningStateViewEntity(state, a.toString(), actionValues.get(a).doubleValue()));
		}
		return entites;
	}

}
