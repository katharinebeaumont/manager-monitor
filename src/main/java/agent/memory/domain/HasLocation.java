package agent.memory.domain;

import org.neo4j.ogm.annotation.EndNode;
import org.neo4j.ogm.annotation.RelationshipEntity;
import org.neo4j.ogm.annotation.StartNode;

@RelationshipEntity(type = "HAS_LOCATION")
public class HasLocation {
	
	public HasLocation() {
		// Empty constructor required as of Neo4j API 2.0.5
	};
	
	@StartNode
    private Entity entity;

    @EndNode
    private Location location;
}