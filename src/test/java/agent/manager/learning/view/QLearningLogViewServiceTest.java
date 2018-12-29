package agent.manager.learning.view;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class QLearningLogViewServiceTest {

	private String test;
	
	@Before
	public void before() {
		test = "2018-10-27 16:04:22.510  INFO 3995 --- [           main] agent.manager.ManagerAgentActions        : New location for mockService-monitoring_agent is Type: local Path:http://localhost:8010"; 
	}
	
	@Test
	public void testProcess() {
		String agentName = "mockService-monitoring_agent";
		QLearningLogViewService service = new QLearningLogViewService();
		List<String> logLines = new ArrayList<String>();
		service.process(test, agentName, logLines);
		
		assertEquals("2018-10-27 16:04:22.510: New location for mockService-monitoring_agent is Type: local Path:http://localhost:8010", logLines.get(0));
	}
}
