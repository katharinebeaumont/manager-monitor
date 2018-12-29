package agent.memory.domain;

import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import agent.common.Heartbeat;

@NodeEntity
public class Application extends Entity implements Heartbeat {
	
	private String name;
	private String jarName;
	private String pid;
	
	@Relationship(type = "HAS_LOCATION", direction = Relationship.OUTGOING)
	private Location location;
	
	public Application(String name, String jarName) {
		this.name = name;
		this.jarName = jarName;
	}
	
	public Application() {
		// Empty constructor required as of Neo4j API 2.0.5
	};
	  
	public String getName() {
		return name;
	}
	
	public String getJarName() {
		return jarName;
	}
	
	public void setLocation(Location location) {
		this.location = location;
	}
	
	public Location getLocation() {
		return location;
	}
	
	public void setPid(String pid) {
		this.pid = pid;
	}
	
	public String getPid() {
		return pid;
	}
}
