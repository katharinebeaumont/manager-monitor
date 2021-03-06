package agent.memory.domain;

import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import agent.common.Heartbeat;

@NodeEntity
public class Monitor extends Entity implements Heartbeat {
	
   private String name;
   private long born;
   private String pid;

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
   
   //TODO: refactor: change monitor name to an agentID not connected to application name
   public String getName() {
	   return name;
   }
   
   public long getBorn() {
	   return born;
   }
   
   public String getPid() {
	   return pid;
   }
   
   public void setPid(String pid) {
	   this.pid = pid;
   }
   
   public Location getLocation() {
	   return location;
   }
	   
   public Application getApplication() {
	   return application;
   }
	
   public void setResponsibility(Application application) {
	   this.application = application;
   }
   
   public void setLocation(Location location) {
	   this.location = location;
   }
}

