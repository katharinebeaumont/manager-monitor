package agent.learning;

//TODO: EntityStatus  fill in the gaps
public abstract class EntityStatus {

	public abstract State getState();
	
	public abstract int getReward();
	
}