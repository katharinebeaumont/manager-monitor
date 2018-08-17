package agent.memory;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import agent.memory.domain.Monitor;

@Repository
public interface MonitorRepository extends Neo4jRepository<Monitor, Long> {

	Monitor findByName(String name);
	
	@Query("MATCH (a:Application {name: '{nameStr}'})--(m:Monitor) RETURN m LIMIT {limit}")
    Monitor monitorForApplication(@Param("nameStr") String name, @Param("limit") int limit);
}
