package agent.monitor.view;

import java.util.List;

public class EventView {
	
	private List<String> events;
	
	public EventView(List<String> events) {
		this.events = events;
	}
	
	public List<String> getEvents() {
		return events;
	}
}
