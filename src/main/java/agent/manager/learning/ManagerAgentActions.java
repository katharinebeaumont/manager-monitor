package agent.manager.learning;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import agent.learning.Action;
import agent.manager.CommandControllerManager;

/*
 * Translates the action selected in the learning process with the commands
 */
@Component
public class ManagerAgentActions {

	private static final Logger log = LoggerFactory.getLogger(ManagerAgentActions.class);
	
	@Autowired
	CommandControllerManager commandCentre;

	public void takeAction(Action actionToTake, String agentID) {
		if (actionToTake.equals(ActionEnum.DO_NOTHING)) {
			return;
		}
		//TODO: PUT RETIRE BACK
		//if (actionToTake.equals(ActionEnum.RETIRE)) {
	//		commandCentre.shutdown(agentID);
	//	}
		if (actionToTake.getActionEnum().equals(ActionEnum.MOVE)) {
			//TODO: need to have location to move to here.
			commandCentre.relocate(agentID, actionToTake.getLocation());
		}
	}
	
	
}
