package agent.monitor.metrics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Very basic in memory implementation of storing metric events from the 
 * application that the monitor is responsible for.
 * Keeps the most current events, at a maximum of 25
 */
@Service
public class MonitorEventStore {
	
	private static final Logger log = LoggerFactory.getLogger(MonitorEventStore.class);
	
	//TODO: make configurable. 
	//@Value("${eventstore.agecutoff.minutes:5}")
	private int maxAgeMinutes = 5;
	
	//@Value("${eventstore.maxevents:25}")
	private int maxSize = 25;
	
	//Don't keep anything older than 5 minutes
	long ageCutOffMillis = 1000*60*maxAgeMinutes;

	//Needs to be synchronised as multiple threads accessing
	List<JSONObject> eventsForMetric = Collections.synchronizedList(new ArrayList<JSONObject>());
	
	public void add(JSONObject event) {
		log.info("Adding " + event + " to Event Store");
		synchronized (eventsForMetric) {
			eventsForMetric.add(event);
		}
		trimToMaxSize();
	}
	
	public JSONObject removeFirst() {
		if (eventsForMetric.size() == 0) {
			JSONObject emptyEvent = new JSONObject();
			try {
				emptyEvent.put("eventStoreEmpty", "null");
				emptyEvent.put("reward", 0);
			} catch (JSONException ex) {
				log.error("Could not create empty event");
			}
			return emptyEvent;
		}
		
		flushOldEvents();
		
		synchronized (eventsForMetric) {
			JSONObject retval = eventsForMetric.remove(0);
			retval.remove("timestamp");
			return retval;
		}
	}

	/*
	 * Just for testing. Not recommended due to potentially slowing
	 * adding and reading from (due to synchronisation)
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<JSONObject> getList() {
		ArrayList<JSONObject> list = new ArrayList<JSONObject>();
		synchronized (eventsForMetric) {
			for (JSONObject obj: eventsForMetric) {
				//Copy existing
				String stringDesc = obj.toString();
				try {
					JSONObject newObj = new JSONObject(stringDesc);
					list.add(newObj);
				} catch (JSONException e) {
					log.error("Could not create new object for " + stringDesc);
				}
			}
		}
		return list;
	}
	
	/*
	 * Remove all events less than the cut off time -
	 * if they are all less than the cut off, leave the latest.
	 */
	private void flushOldEvents() {
		long currentTimeMillis = System.currentTimeMillis();
		long timeCutOff = currentTimeMillis - ageCutOffMillis;
		boolean flush = true;
		synchronized (eventsForMetric) {
			while (flush) {
				if (eventsForMetric.size() == 1) {
					flush = false;
					break;
				}
				JSONObject event = eventsForMetric.get(0);
				long timestamp;
				try {
					timestamp = event.getLong("timestamp");
					if (timestamp < timeCutOff) {
						eventsForMetric.remove(0);
					} else {
						flush = false;
					}
				} catch (JSONException e) {
					log.error("Could not parse timestamp for event " + event);
				}
			}
		}
	}
	
	/*
	 * So long as the event store is greater than the maximum permitted size,
	 * removes the oldest one
	 */
	private void trimToMaxSize() {
		synchronized (eventsForMetric) {
			while(eventsForMetric.size() > maxSize) {
				//this shifts all indexes along, so don't need to increment index
				eventsForMetric.remove(0);
			}
		}
		
	}
}
