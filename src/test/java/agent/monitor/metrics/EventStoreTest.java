package agent.monitor.metrics;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;


public class EventStoreTest {

	@Test
	public void testAdd() {
		MonitorEventStore eventStore = new MonitorEventStore();
		long time = System.currentTimeMillis();
		String eventName = "test event";
		JSONObject jsonObj = new JSONObject();
		try {
			jsonObj.put("timestamp", time);
			jsonObj.put("name", eventName);
		} catch (JSONException e) {
			e.printStackTrace();
			fail();
		}

		eventStore.add(jsonObj);
		JSONObject retVal = eventStore.removeFirst();
		try {
			assertEquals(eventName, retVal.get("name"));
		} catch (JSONException e) {
			fail();
		}
	}
	
	@Test
	public void testRemoveFirst() {
		MonitorEventStore eventStore = new MonitorEventStore();
		long currentTime = System.currentTimeMillis();
		long time = currentTime - 3000;
		long timeNext = currentTime - 5000;
		long timeThird = currentTime - 7000;
		String eventName = "test event";
		JSONObject event1 = new JSONObject();
		JSONObject event2 = new JSONObject();
		JSONObject event3 = new JSONObject();
		try {
			event1.put("timestamp", time);
			event1.put("name", eventName);
			event2.put("timestamp", timeNext);
			event2.put("name", "test event 2");
			event3.put("timestamp", timeThird);
			event3.put("name",  "test event 3");
		} catch (JSONException e) {
			e.printStackTrace();
			fail();
		}

		eventStore.add(event1);
		eventStore.add(event2);
		eventStore.add(event3);
		JSONObject retVal = eventStore.removeFirst();
		try {
			assertEquals(eventName, retVal.get("name"));
		} catch (JSONException e) {
			fail();
		}
	}
	
	@Test
	public void testRemoveTwice() {
		MonitorEventStore eventStore = new MonitorEventStore();
		long currentTime = System.currentTimeMillis();
		long time = currentTime - 3000;
		long timeNext = currentTime - 5000;
		long timeThird = currentTime - 7000;
		String eventName = "test event";
		JSONObject event1 = new JSONObject();
		JSONObject event2 = new JSONObject();
		JSONObject event3 = new JSONObject();
		try {
			event1.put("timestamp", time);
			event1.put("name", eventName);
			event2.put("timestamp", timeNext);
			event2.put("name", "test event 2");
			event3.put("timestamp", timeThird);
			event3.put("name",  "test event 3");
		} catch (JSONException e) {
			e.printStackTrace();
			fail();
		}

		eventStore.add(event1);
		eventStore.add(event2);
		eventStore.add(event3);
		JSONObject retVal = eventStore.removeFirst();
		try {
			assertEquals(eventName, retVal.get("name"));
		} catch (JSONException e) {
			fail();
		}
		JSONObject retVal2 = eventStore.removeFirst();
		try {
			assertEquals("test event 2", retVal2.get("name"));
		} catch (JSONException e) {
			fail();
		}
	}
	/*
	@Test
	public void testFlushOld() {
		long startTimeTest = System.currentTimeMillis();
		
		MonitorEventStore eventStore = new MonitorEventStore();
		long sixMinutes = (1000*6*60);
		long fiveMinutesAgo = startTimeTest - (1000*5*60);
		
		generateOldAndCurrentEvents(eventStore, sixMinutes);
		
		ArrayList<JSONObject> list = eventStore.getList();
		assertEquals(25, list.size());
		
		JSONObject removed = eventStore.removeFirst();
		try {
			long ageOfRemoved = removed.getLong("timestamp");
			System.out.println("Removed is " + removed.getLong("mins old") + " minutes old");
			assertTrue(ageOfRemoved >= fiveMinutesAgo);
		} catch (JSONException e1) {
			fail();
		}
		
		list = eventStore.getList();
		//Check remaining are within last 5 minutes
		for (JSONObject event: list) {
			try {
				assertTrue(event.getLong("timestamp") >= fiveMinutesAgo);
			} catch (JSONException e) {
				fail();
			}
		}
		assertEquals(9, list.size());
	}
	
	@Test
	public void testTrimOver25() {
		long startTimeTest = System.currentTimeMillis();
		
		MonitorEventStore eventStore = new MonitorEventStore();
		long tenMinutes = (1000*10*60);
		long fourMinutes = (1000*4*60);
		long fiveMinutesAgo = startTimeTest - (1000*5*60);
		long fourMinutesAgo = startTimeTest - fourMinutes;
		
		//Adds 25 events
		generateOldAndCurrentEvents(eventStore, tenMinutes);
		ArrayList<JSONObject> list = eventStore.getList();
		assertEquals(25, list.size());
		
		//Adds another 25 events
		generateOldAndCurrentEvents(eventStore, fourMinutes);
		
		//But should keep at 25
		list = eventStore.getList();
		assertEquals(25, list.size());
		
		JSONObject removed = eventStore.removeFirst();
		try {
			long ageOfRemoved = removed.getLong("timestamp");
			System.out.println("Removed is " + removed.getLong("mins old") + " minutes old");
			//Expecting removed event to be within the last 5 minutes
			assertTrue(ageOfRemoved > fiveMinutesAgo);
		} catch (JSONException e1) {
			fail();
		}
		
		list = eventStore.getList();
		//Check remaining are within last 4 minutes
		for (JSONObject event: list) {
			try {
				assertTrue(event.getLong("timestamp") >= fourMinutesAgo);
			} catch (JSONException e) {
				fail();
			}
		}
		assertEquals(24, list.size());
	}
	
	private void generateOldAndCurrentEvents(MonitorEventStore es, long initialAdjustment) {
		long adjustment = initialAdjustment;
		
		long startTime = System.currentTimeMillis();
		for (int i = 0; i<25; i++) {
			if (i == 10) {
				//Make younger by a minute
				adjustment -= 1000*60;
				
			} else if (i == 15) {
				//Make younger by a minute
				adjustment -= 1000*60;
				
			} else if (i == 20) {
				//Make younger by a minute
				adjustment -= 1000*60;
				
			}
			long currentTimeAdjusted = startTime - adjustment;
						
			JSONObject jsonObj = new JSONObject();
			try {
				jsonObj.put("timestamp", currentTimeAdjusted);	
				jsonObj.put("mins old", adjustment/(1000*60));
			} catch (JSONException e) {
				e.printStackTrace();
				fail();
			}
			es.add(jsonObj);
			
		}
	}
	
	@Test
	public void testFlushOldWhenAllOld() {
		MonitorEventStore eventStore = new MonitorEventStore();
		//Initial adjustment is 10 minutes
		generateOldAndCurrentEvents(eventStore, 1000*60*10);
		
		long startTimeTest = System.currentTimeMillis();
		
		ArrayList<JSONObject> list = eventStore.getList();
		assertEquals(25, list.size());
		
		long fiveMinutesAgo = startTimeTest - (1000*5*60);
		JSONObject removed = eventStore.removeFirst();
		list = eventStore.getList();
		assertEquals(0, list.size());
		try {
			assertTrue(removed.getLong("timestamp") < fiveMinutesAgo);
		} catch (JSONException e1) {
			fail();
		}
	}
	
	@Test
	public void testRemoveTwiceWhenOnly1Events() {
		MonitorEventStore eventStore = new MonitorEventStore();
		long currentTime = System.currentTimeMillis();
		long time = currentTime - 3000;
		
		String eventName = "test event";
		JSONObject event1 = new JSONObject();
		try {
			event1.put("timestamp", time);
			event1.put("name", eventName);
		} catch (JSONException e) {
			e.printStackTrace();
			fail();
		}

		eventStore.add(event1);
		JSONObject retVal = eventStore.removeFirst();
		try {
			assertEquals(eventName, retVal.get("name"));
			assertEquals(time, retVal.get("timestamp"));
		} catch (JSONException e) {
			fail();
		}
		JSONObject retVal2 = eventStore.removeFirst();
		assertEquals("{\"reward\":0,\"eventStoreEmpty\":\"null\"}", retVal2.toString());
	}*/
	
}
