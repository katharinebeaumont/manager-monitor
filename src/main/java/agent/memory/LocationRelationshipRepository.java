package agent.memory;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import agent.memory.domain.HasLocation;

@Repository
public interface LocationRelationshipRepository extends Neo4jRepository<HasLocation, Long> {

	@Query("MATCH (a:Entity {name: {entity}})-[r:HAS_LOCATION]-(b:Location) DELETE r")
    void deleteRelationship(@Param("entity") String entity);

}
