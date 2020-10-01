package agent.monitor;

import static org.junit.Assert.assertEquals;

import org.json.*;
import org.junit.jupiter.api.*;

import agent.learning.State;

public class ApplicationStatusTest {

	String testResponse1 = "{\"name\":\"jvm.memory.max\",\"measurements\":[{\"statistic\":\"VALUE\",\"value\":3.458203647E9}],\"availableTags\":[{\"tag\":\"area\",\"values\":[\"heap\",\"nonheap\"]},{\"tag\":\"id\",\"values\":[\"Compressed Class Space\",\"PS Survivor Space\",\"PS Old Gen\",\"Metaspace\",\"PS Eden Space\",\"Code Cache\"]}]}";
	String testResponse2 = "{\"name\":\"jvm.memory.max\",\"measurements\":[{\"statistic\":\"VALUE\",\"value\":3.458203647E9}],\"availableTags\":[{\"tag\":\"area\",\"values\":[\"heap\",\"nonheap\"]},{\"tag\":\"id\",\"values\":[\"Compressed Class Space\",\"PS Survivor Space\",\"PS Old Gen\",\"Metaspace\",\"PS Eden Space\",\"Code Cache\"]}]}";
	String testResponse3 = "{\"name\":\"jvm.memory.max\",\"measurements\":[{\"statistic\":\"VALUE\",\"value\":3.458203647E9}],\"availableTags\":[{\"tag\":\"area\",\"values\":[\"heap\",\"nonheap\"]},{\"tag\":\"id\",\"values\":[\"Compressed Class Space\",\"PS Survivor Space\",\"PS Old Gen\",\"Metaspace\",\"PS Eden Space\",\"Code Cache\"]}]}";
	
	@Test
	void testParse() throws JSONException {
		ApplicationStatus app = new ApplicationStatus("test", testResponse1);
		
		State state = app.getState();
		JSONObject stateDesc = state.getStateDesc();
		assertEquals(2, stateDesc.length());
		assertEquals("jvm.memory.max", stateDesc.get("name"));
		assertEquals(3.458203647E9, stateDesc.get("value"));
		
	}
	
}
