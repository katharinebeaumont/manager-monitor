package agent.monitor.metrics;

/**
 * Ideally: this would use regex and a set of rules to find the name and value
 * from different text. At the moment it relies on using the Spring Actuator feedback
 */
public class EventParser {

	/*
	 * This is just to get something basic working.
	 */
	public static Event parse(String event) {
		int indexOfName = event.indexOf("\"name\":\"") + 8;
		int indexEndName = event.indexOf("\",\"measurements");
		String name = event.substring(indexOfName, indexEndName);
		
		int indexOfValue = event.indexOf("\"value\":") + 8;
		String afterValue = event.substring(indexOfValue);
		int indexOfEnd = afterValue.indexOf("}");
		String value = afterValue.substring(0, indexOfEnd);
		return new Event(name, value);
	}
	
}
