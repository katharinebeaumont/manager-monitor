package agent.memory.domain;

import org.neo4j.ogm.annotation.EndNode;
import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.RelationshipEntity;
import org.neo4j.ogm.annotation.StartNode;

@RelationshipEntity(type = "RESPONSIBLE_FOR")
public class Responsibility extends Entity {

	public Responsibility() {
		// Empty constructor required as of Neo4j API 2.0.5
	};
	
	@StartNode
    private Monitor slave;

    @EndNode
    private Application application;
}
