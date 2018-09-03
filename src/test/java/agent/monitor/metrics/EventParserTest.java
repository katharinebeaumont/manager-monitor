package agent.monitor.metrics;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class EventParserTest {

	@Test
	public void testParse() {
		//Arrange
		String testValue = "{\"name\":\"jvm.memory.used\",\"measurements\":"
				+ "[{\"statistic\":\"VALUE\",\"value\":1.29421192E8}],\"availableTags\""
				+ ":[{\"tag\":\"area\",\"values\":[\"heap\",\"nonheap\"]},"
				+ "{\"tag\":\"id\",\"values\":[\"Compressed Class Space\","
				+ "\"PS Survivor Space\",\"PS Old Gen\",\"Metaspace\","
				+ "\"PS Eden Space\",\"Code Cache\"]}]}";
		
		//Act
		Event e = EventParser.parse(testValue);
		
		//Assert
		assertEquals("name:jvm.memory.used, value:1.29421192E8", e.toString());
	}
}
