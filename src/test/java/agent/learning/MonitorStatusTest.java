package agent.learning;

import static org.junit.Assert.assertEquals;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import agent.manager.learning.MonitorStatus;
import agent.memory.domain.Location;

public class MonitorStatusTest {

	String response = "{\"response\":\"{\\\"reward\\\":-1,\\\"jvm.memory.used\\\":\\\"high\\\",\\\"timestamp\\\":1602013186713}\"}";
	
	
	@Test
	public void testConstructor() throws JSONException {
		String name = "agent0";
		Location loc1 = new Location("http://localhost", "local", 8000, false);
		
		MonitorStatus status = new MonitorStatus(name, response, loc1);
		
		assertEquals(name, status.agentName());
		assertEquals(-1, status.getReward());
		
		State s = status.getState();
		JSONObject stateDesc = s.getStateDesc();
		assertEquals(loc1.toString(), stateDesc.get("location"));
		assertEquals("high", stateDesc.get("jvm.memory.used"));
		assertEquals(new Long("1602013186713"), stateDesc.get("timestamp"));
			
	}
}
