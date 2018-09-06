package agent.manager.learning;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * Testing the code in FeedbackProcessor.
 */
public class FeedbackProcessorTest {

	String response = "{\"response\":\"2;jvm.memory.max:3.465019391E9;system.cpu.usage:0.31859883236030023;jvm.memory.used:2.23495504E8\"}";
	
	String shortResponse = "{\"response\":\"0\"}";
	
	@Test
	public void testProcessReward() throws Exception {
		
		//Arrange
		int expectedReward = 2;
		
		//Act
		int reward = FeedbackProcessor.processReward(response);
		
		//Assert
		assertEquals(expectedReward, reward);
		
	}
	
	@Test
	public void testProcessRewardForShortResponse() throws Exception {
		
		//Arrange
		int expectedReward = 0;
		
		//Act
		int reward = FeedbackProcessor.processReward(shortResponse);
		
		//Assert
		assertEquals(expectedReward, reward);
		
	}
	
	@Test
	public void testProcessStatus() throws Exception {
		//Arrange
		String[] expectedStatusDesc = new String[] {"jvm.memory.max:3.465019391E9","system.cpu.usage:0.31859883236030023","jvm.memory.used:2.23495504E8"};
		State expectedStatus = new State(expectedStatusDesc);
		
		//Act
		State status = FeedbackProcessor.processState(response);
		
		//Assert
		
		assertTrue(expectedStatus.equals(status));
	}
}
