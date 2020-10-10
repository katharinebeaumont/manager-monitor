package agent.learning;

import static org.junit.Assert.assertEquals;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

public class StateTest {
	String response = "{\"response\":{\"reward\":-1,\"jvm.memory.used\":\"high\",\"timestamp\":1602013186713}}";
	String noResponseStringSurrounding = "{\"reward\":-1,\"jvm.memory.used\":\"high\",\"timestamp\":1602013186713}";
	
	@Test
	public void testParseStateDesc() throws JSONException {
		State s = new State(response);
		
		assertEquals(-1, s.getStateDesc().get("reward"));
		assertEquals("high", s.getStateDesc().get("jvm.memory.used"));
		assertEquals(new Long("1602013186713"), s.getStateDesc().get("timestamp"));
		
	}
	
	@Test
	public void testParseStateDescNoResponseString() throws JSONException {
		State s = new State(noResponseStringSurrounding);
		JSONObject stateDesc = s.getStateDesc();
		assertEquals(-1, stateDesc.get("reward"));
		assertEquals("high", stateDesc.get("jvm.memory.used"));
		assertEquals(new Long("1602013186713"), stateDesc.get("timestamp"));
		
	}
}
