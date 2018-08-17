package agent.memory.domain;

import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

@NodeEntity
public class Monitor extends Entity {
	
   private String name;
   private long born;

   @Relationship(type = "RESPONSIBLE_FOR", direction = Relationship.OUTGOING)
   private Application application;

   @Relationship(type = "HAS_LOCATION", direction = Relationship.OUTGOING)
   private Location location;
   
   public Monitor() {
	   //empty constructor required by Neo4j
   }
   
   public Monitor(String applicationName) {
	   this.name = applicationName + "-monitoring_agent";
	   this.born = System.currentTimeMillis();
   }
   
   public String getName() {
	   return name;
   }
   
   public long getBorn() {
	   return born;
   }
   
	public Location getLocation() {
		return location;
	}
	
	public void setResponsibility(Application application) {
		this.application = application;
	}
   
	public void setLocation(Location location) {
		this.location = location;
	}
}

