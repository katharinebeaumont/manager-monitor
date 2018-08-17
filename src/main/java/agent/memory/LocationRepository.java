package agent.memory;

import java.util.Collection;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import agent.memory.domain.Location;

@Repository
public interface LocationRepository extends Neo4jRepository<Location, Long> {

	@Query("MATCH (a:Application {name: {nameStr}})--(l:Location) RETURN l LIMIT {limit}")
    Location locationForApplication(@Param("nameStr") String name, @Param("limit") int limit);

	@Query("MATCH (m:Monitor {name: {nameStr}})--(l:Location) RETURN l LIMIT {limit}")
    Location locationForMonitor(@Param("nameStr") String name, @Param("limit") int limit);

	@Query("MATCH (n:Location) RETURN n LIMIT {limit}")
    Collection<Location> graphLocation(@Param("limit") int limit);

	@Query("MATCH (n:Location) WHERE NOT (n)<-[:HAS_LOCATION]-(:Entity) RETURN n LIMIT {limit}")
	Collection<Location> freeLocation(@Param("limit") int limit);
}
