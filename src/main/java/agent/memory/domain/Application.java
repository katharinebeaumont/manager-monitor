package agent.memory.domain;

import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

@NodeEntity
public class Application extends Entity {
	
	private String name;
	
	@Relationship(type = "HAS_LOCATION", direction = Relationship.OUTGOING)
	private Location location;
	
	public Application(String name) {
		this.name = name;
	}
	
	public Application() {
		// Empty constructor required as of Neo4j API 2.0.5
	};
	  
	public String getName() {
		return name;
	}
	
	public void setLocation(Location location) {
		this.location = location;
	}
	
	public Location getLocation() {
		return location;
	}
}
