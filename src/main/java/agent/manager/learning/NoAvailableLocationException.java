package agent.manager.learning;

public class NoAvailableLocationException extends Exception {
	public NoAvailableLocationException(String name) {
		super("No location available for " + name);
	}
}
