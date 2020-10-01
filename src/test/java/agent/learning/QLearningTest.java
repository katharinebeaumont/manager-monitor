package agent.learning;


import agent.manager.learning.ActionEnum;
import agent.manager.learning.MonitorStatus;
import agent.manager.learning.QLearningManager;
import agent.memory.domain.Location;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class QLearningTest {

	QLearningManager qLearning;
    MonitorStatus monitorStatus1;
    MonitorStatus monitorStatus2;
    MonitorStatus monitorStatus3;
    MonitorStatus monitorStatus4;

    @BeforeEach
    void setUp() {
    	qLearning = Mockito.spy(new QLearningManager("agent123", -10, 0.4, 0.5, 0.1, false));
        //Locations not important in these tests.
    	Action moveTo2 = new Action(ActionEnum.MOVE, new Location("first location"));
		List<Action> actionsForLocation1 = Arrays.asList(new Action(ActionEnum.DO_NOTHING), moveTo2);
    	Mockito.doReturn(actionsForLocation1).when(qLearning).updateAvailableLocations("first state");
    	List<Action> actionsForLocation2 = Arrays.asList(new Action(ActionEnum.DO_NOTHING), new Action(ActionEnum.MOVE, new Location("second location")));
    	Mockito.doReturn(actionsForLocation2).when(qLearning).updateAvailableLocations("second state");
    	Mockito.doReturn(actionsForLocation2).when(qLearning).updateAvailableLocations("third state");
    	Mockito.doReturn(actionsForLocation2).when(qLearning).updateAvailableLocations("forth state");
	
        monitorStatus1 = new MonitorStatus("agent123", "{\"location\":\"first state\",\"reward\":2}");
        monitorStatus2 = new MonitorStatus("agent123", "{\"location\":\"second state\",\"reward\":4}");
        monitorStatus3 = new MonitorStatus("agent123", "{\"location\":\"third state\",\"reward\":-10}");
        monitorStatus4 = new MonitorStatus("agent123", "{\"location\":\"forth state\",\"reward\":-10}");
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void testStartNewEpisode() {
        //Arrange
        qLearning.episodeStep(monitorStatus1);
        qLearning.episodeStep(monitorStatus2);
        double totalValue = qLearning.getCurrentValue();
        Action a = qLearning.getPreviousAction();
        State s = qLearning.getPreviousState();
        List<List<String>> steps = qLearning.getEpisodeSteps();
        int sizeEpisode = steps.size();

        //Act
        qLearning.startNewEpisode();
        
        //Assert
        assertTrue(totalValue > 0);
        assertTrue(s != State.initialState());
        assertEquals(ActionEnum.DO_NOTHING, qLearning.getPreviousAction().getActionEnum());
        assertEquals(State.initialState(), qLearning.getPreviousState());
        assertEquals(0, qLearning.getCurrentValue());
        List<List<String>> stepsAfterNewEpisode = qLearning.getEpisodeSteps();
        assertEquals(sizeEpisode + 1, stepsAfterNewEpisode.size());
    }

    @Test
    void testInitialAction() {
        //Act
        Action firstReturnedAction = qLearning.getPreviousAction();
        //Assert
        assertEquals(firstReturnedAction.getActionEnum(), ActionEnum.DO_NOTHING);
    }

    @Test
    void testGetAction() {

        //Act
        Action firstReturnedAction = qLearning.episodeStep(monitorStatus1);
        Action getAction = qLearning.getPreviousAction();

        //Assert
        assertEquals(firstReturnedAction, getAction);

        //Check it updates
        //Act
        Action secondReturnedAction = qLearning.episodeStep(monitorStatus2);
        getAction = qLearning.getPreviousAction();

        //Assert
        assertEquals(secondReturnedAction, getAction);
    }

    @Test
    void testAddToSteps() {
    	
        //Arrange
        //First status is received from the environment, reward 2
        //And the action is decided based on that
        Action firstReturnedAction = qLearning.episodeStep(monitorStatus1);
        State state1 = State.initialState();
        String stateDesc = state1.toString();
        String actionDesc = ActionEnum.DO_NOTHING.toString();
        int reward = monitorStatus1.getReward();

        List<String> episode0 = (List<String>) qLearning.getEpisodeSteps().get(0);
        String step1 = episode0.get(0);
        assertTrue(step1.contains(stateDesc) && step1.contains(actionDesc) && step1.contains("Reward: " + reward));
        stateDesc = monitorStatus1.getState().toString();
        actionDesc = firstReturnedAction.toString();
        //Second status is received from the environment, reward 4
        reward = monitorStatus2.getReward();
        Action secondReturnedAction = qLearning.episodeStep(monitorStatus2);
        episode0 = (List<String>) qLearning.getEpisodeSteps().get(0);

        String step2 = episode0.get(1);
        assertTrue(step2.contains(stateDesc) && step2.contains(actionDesc) && step2.contains("Reward: " + reward));
        System.out.println(qLearning.getEpisodeSteps());
    }

    @Test
    void testContinueEpsiode() {
        //1. Monitor status 3 has a value of -10.
        // Alpha is 0.5 so this will be stored as -5
        // The episode lower threshold is -10
    	Action a1 = qLearning.episodeStep(monitorStatus3);
    	a1 = qLearning.episodeStep(monitorStatus3);
        
    	double currentValueA1 = qLearning.getCurrentValue();
        List<List<String>> steps = qLearning.getEpisodeSteps();
        assertEquals(1, steps.size());
        assertEquals(-5, currentValueA1);

        //A2 should be null as a new episode is started.
        // This is as, again we get a value of -10, x 0.5 = -5
        // The lower thresholid is met, and a new episode is triggered
        Action a2 = qLearning.episodeStep(monitorStatus4);
        double currentValueA2 = qLearning.getCurrentValue();
        assertTrue(a2 == null);
        assertEquals(0, currentValueA2);
        assertEquals(2, steps.size());

        //Now we should still be in the second episode, and monitor status 1
        // has a positive value so we expect to stay here.
        Action a3 = qLearning.episodeStep(monitorStatus1);
        double currentValueA3 = qLearning.getCurrentValue();
        //Assert
        assertTrue(a3 != null);
        assertEquals(2, steps.size());
    }

    /*
     * For UI
     */
    @Test
    void getQTable() {
    }

    @Test
    void getCurrentValue() {
    }

    @Test
    void getEpisodeSteps() {
    }
}