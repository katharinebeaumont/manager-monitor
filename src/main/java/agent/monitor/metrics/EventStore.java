package agent.monitor.metrics;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Very basic in memory implementation of storing metric events from the 
 * application that the monitor is responsible for.
 */
public class EventStore {
	
	private static final Logger log = LoggerFactory.getLogger(EventStore.class);
	
	HashMap<String, LinkedList<String>> events = new HashMap();
	
	public void add(String event) {
		
		log.info("Adding " + event + " to Event Store");
		Event e = EventParser.parse(event);
		LinkedList<String> values = events.get(e.getName());
		if (values == null) {
			log.info("No linked list for " + e.getName() + ": creating.");
			values = new LinkedList<String>();
		}
		values.addLast(e.getValue());
		events.put(e.getName(), values);
	}
	
	public Set<String> getEventNames() {
		return events.keySet();
	}
	
	public String removeFirst(String name) {
		LinkedList<String> values = events.get(name);
		String retval = "";
		try {
			retval = values.removeFirst();
		} catch (NullPointerException | NoSuchElementException ex) {
			log.debug("No latest value for " + name + ": returning empty String.");
		}
		return retval; 
	}
}
