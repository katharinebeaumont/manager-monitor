package agent.monitor.metrics;

/**
 * Wrapped in an object so can add additional values like event timestamp, origin,
 * etc
 */
public class Event {

	String name;
	
	String value;
	
	public Event(String name, String value) {
		this.name = name;
		this.value = value;
	}
	
	@Override
	public String toString() {
		return "name:" + name + ", value:" + value;
	}
	
	public String getName() {
		return name;
	}
	
	public String getValue( ) {
		return value;
	}
}
