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
		log.info("#Added " + values.size() + " to Event Store for " + event);
	}
	
	public Set<String> getEventNames() {
		return events.keySet();
	}
	
	public String removeFirst(String name) {
		LinkedList<String> values = events.get(name);
		String retval = "";
		try {
			retval = values.removeFirst();
			log.info("Removing and returning " + retval);
		} catch (NullPointerException | NoSuchElementException ex) {
			log.debug("No latest value for " + name + ": returning empty String.");
		}
		return retval; 
	}
	
	/*
	 * Deals with the fact that the event store can fill up very quickly
	 * and events aren't removed fast enough. Want current information, so 
	 * just remove the events.
	 */
	public String removeLast(String name) {
		LinkedList<String> values = events.get(name);
		String retval = "";
		try {
			retval = values.removeLast();
			log.info("Removing and returning " + retval);
			if (values.size() > 10) {
				log.info("Have 10 old events for " + name + ". Clearing out previous events");
				values = new LinkedList<String>();
				events.put(name, values);
			}
			
		} catch (NullPointerException | NoSuchElementException ex) {
			log.debug("No latest value for " + name + ": returning empty String.");
		}
		return retval; 
	}
}
