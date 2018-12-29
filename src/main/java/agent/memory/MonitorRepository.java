package agent.memory;

import java.util.Collection;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import agent.memory.domain.Application;
import agent.memory.domain.Monitor;

@Repository
public interface MonitorRepository extends Neo4jRepository<Monitor, Long> {

	@Query("MATCH (m:Monitor {name: {monitorName}}) RETURN m LIMIT 1")
	Monitor findByName(@Param("monitorName") String name);
	
	@Query("MATCH (a:Application {name: {nameStr}})--(m:Monitor) RETURN m LIMIT {limit}")
    Monitor monitorForApplication(@Param("nameStr") String name, @Param("limit") int limit);

	@Query("MATCH (m:Monitor) RETURN m LIMIT {limit}")
    Collection<Monitor> getAll(@Param("limit") int limit);
	
}
