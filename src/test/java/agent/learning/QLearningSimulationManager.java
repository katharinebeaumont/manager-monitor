package agent.learning;

import agent.learning.view.QLearningStateViewEntity;
import agent.learning.view.QLearningStateViewEntityBuilder;
import agent.manager.learning.ActionEnum;
import agent.manager.learning.MonitorStatus;
import agent.manager.learning.QLearningManager;
import agent.memory.DBInterface;
import agent.memory.domain.Location;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class QLearningSimulationManager {

    private static final Logger log = LoggerFactory.getLogger(QLearningSimulationManager.class);

    /*
        Reminder: State options from STATE_ACTION_GUIDE.md:
        - Monitor identifier
        - Location: which machine and port the monitor is located on
        - Is Duplicate flag: are there multiple monitors observing duplicate microservices
        - Microservice internals:
          - JVM memory
          - Network latency

        Possible actions:
        - Move location
        - Do nothing
        - Retire service

        Environmental feedback:
             0: service is up and running
            -1: service is underperforming
            -5: service is down

        Ideally: learns to associate 'Do nothing' with feedback of 0
     */
    /*
     * Learning parameters
     */
    String agentName = "Agent0";
    int lowerThreshold = -2;
    double gamma = 0.9;
    double alpha = 0.01;
    double epsilon = 0.20;
    
    /*
     * Test parameters
     */
    int noEpisodesToTrain = 1000;
    
    /*
     * Messy but have to do this in order to see how many times this
     * is successful (cannot use @After annotation in the event of test
     * failures, so cannot use a loop to run test multiple times).
     */
    @Test
    public void runTest1() {
    	simulateManagerQLearning();
    }
    @Test
    public void runTest2() {
    	simulateManagerQLearning();
    }
    @Test
    public void runTest3() {
    	simulateManagerQLearning();
    }
    @Test
    public void runTest4() {
    	simulateManagerQLearning();
    }
    @Test
    public void runTest5() {
    	simulateManagerQLearning();
    }

    private void simulateManagerQLearning() {
    	
    	//QLearning calls the database in DBInterface to see what
    	// available locations there are, and creates the possible actions 
    	// an agent can take based on these. It (database call) needs mocking
    	// in this test
    	DBInterface dbInterface = Mockito.spy(new DBInterface());
        
    	QLearningManager qLearning = Mockito.spy(new QLearningManager(dbInterface, agentName, lowerThreshold, gamma, alpha, epsilon, true));
        mockPotentialActions(qLearning);
    	
    	//Keep track of the number of times an action was taken
    	HashMap<Action, Integer> actionCount = new HashMap<Action, Integer>();
    	//Same for the states
    	HashMap<MonitorStatus, Integer> stateCount = new HashMap<MonitorStatus, Integer>();
    	
    	QLearningSimulationStates simulationProvider = new QLearningSimulationStates(agentName);
        //The iteration number (total steps across all episodes)
        int i = 0;
        //The episode number
        int episodeCount = 1;
        //Initialise the first action
        Action actionTaken = new Action(ActionEnum.DO_NOTHING);
        
        while (episodeCount <= noEpisodesToTrain) {
            i++;
            
            //The simulationProvider provides the environmental feedback:
            // the state and reward
            MonitorStatus state = simulationProvider.getState(actionTaken);
			
            //Feed the state into QLearning and get the action
            actionTaken = qLearning.episodeStep(state);
            //This means the episode is over
            if (actionTaken == null) {
                episodeCount++;
                log.debug("Starting new episode: " + episodeCount);
                actionTaken = new Action(ActionEnum.DO_NOTHING);
            }   
            
            
            updateActionCount(actionCount, actionTaken);
            updateStateCount(stateCount, state);
        }
        
        log.info("Finished! Taken " + i + " steps over " + noEpisodesToTrain + " episodes.");
        
        log.info("Actions over time: ");
        for (Action a: actionCount.keySet()) {
        	int count = actionCount.get(a);
        	log.info("Took action: " + a + " " + count + " times");
        }
        log.info("States over time: ");
        for (MonitorStatus s: stateCount.keySet()) {
        	int count = stateCount.get(s);
        	log.info("Was in state: " + s.toString() + " " + count + " times");
        }
    
        //Now test is over, show results in console:
        try {
            assertAndLogResults(qLearning.getQTable());
        } catch (Exception e) {
            fail("Test has failed due to exception: " + e.getMessage());
        }
    }

	private void updateStateCount(HashMap<MonitorStatus, Integer> stateCount, MonitorStatus state) {
		int s_count = stateCount.getOrDefault(state, 0);
		s_count++;
		stateCount.put(state, s_count);
	}

	private void updateActionCount(HashMap<Action, Integer> actionCount, Action actionTaken) {
		int count = actionCount.getOrDefault(actionTaken, 0);
		count++;
		actionCount.put(actionTaken, count);
	}

	private void mockPotentialActions(QLearningManager qLearning) {
		Action moveTo2 = new Action(ActionEnum.MOVE, new Location("2"));
		List<Action> actionsForLocation1 = Arrays.asList(new Action(ActionEnum.DO_NOTHING), moveTo2);
    	Mockito.doReturn(actionsForLocation1).when(qLearning).updateAvailableLocations("1");
    	List<Action> actionsForLocation2 = Arrays.asList(new Action(ActionEnum.DO_NOTHING), new Action(ActionEnum.MOVE, new Location("1")));
    	Mockito.doReturn(actionsForLocation2).when(qLearning).updateAvailableLocations("2");
	}

    private void assertAndLogResults(QTable table) throws Exception {
        HashMap<State, HashMap> map = table.getQTable();
        log.info("******************************");
        log.info("Q table for " + agentName + " is size " + map.size());
        log.info("Reminder: JVM Memory 0 = bad, Latency 1 = bad");
        for (State s: map.keySet()) {
        	JSONObject stateDesc = s.getStateDesc();
            int location = (Integer) stateDesc.get("location");
            log.info("---------- State ----------");
            
            if (stateDesc.has("jvmMemory")) {
            	log.info("Location: " + location + ", JVM Memory: " + stateDesc.getDouble("jvmMemory"));
            } else {
            	log.info("Location: " + location + ", Latency: " + stateDesc.getDouble("latency"));
            }
            List<QLearningStateViewEntity> views = QLearningStateViewEntityBuilder.build(s, map.get(s));
            for (QLearningStateViewEntity view: views) {
                String a = view.getAction();
                double v = view.getValue();
                if (a.equals(ActionEnum.DO_NOTHING.toString())) {
                    log.info("Do nothing:" + v);
                } else if (a.contains(ActionEnum.MOVE.toString())) {
                    log.info(a + ":" + v);
                } else {
                    throw new Exception("Unaccounted for action: " + a);
                }
            }
        }

        for (State s: map.keySet()) {
            double valueDoNothing = 0;
            double valueMove = 0;
            //State: location + ":" + jvmMemory + ":" + latency;
            List<QLearningStateViewEntity> views = QLearningStateViewEntityBuilder.build(s, map.get(s));
            for (QLearningStateViewEntity view: views) {
                String a = view.getAction();
                double v = view.getValue();
                if (a.equals(ActionEnum.DO_NOTHING.toString())) {
                    valueDoNothing = v;
                } else if (a.contains(ActionEnum.MOVE.toString())) {
                    valueMove = v;
                } else {
                    throw new Exception("Unaccounted for action: " + a);
                }
            }

            //We are expecting: 
            // - low reward when JVM memory is 0, and the agent
            //   does nothing
            // - low reward when latency is high, and the agent
            //   does nothing
            // - Location 2 is set up to always have high latency,
            //   so we always expect it to have a higher reward
            //   for moving to location 1 (covered in assertions 
            //   by checking if latency is high).
            JSONObject stateDesc = s.getStateDesc();
            int location = (Integer) stateDesc.get("location");
            if (location == 2) {
            	assertTrue(valueDoNothing < valueMove);
            } else if (location == 1) {
            	assertTrue(valueDoNothing > valueMove);
            } else {
            	fail(); //Shouldn't be here
            }
        }
        
        log.info("******************************");
    }
}
