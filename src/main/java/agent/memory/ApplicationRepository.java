package agent.memory;

import java.util.Collection;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import agent.memory.domain.Application;

@Repository
public interface ApplicationRepository extends Neo4jRepository<Application, Long>{
	
    Application findByName(@Param("name") String name);

    @Query("MATCH (m:Monitor)<-[r:RESPONSIBLE_FOR]-(a:Application) RETURN m,r,a LIMIT {limit}")
    Collection<Application> graph(@Param("limit") int limit);
	
    @Query("MATCH (n:Application) RETURN n LIMIT {limit}")
    Collection<Application> graphApplication(@Param("limit") int limit);

    @Query("MATCH (n:Application)--(m:Monitor {name: {monitorName}}) RETURN n LIMIT {limit}")
	Application findByMonitor(@Param("monitorName") String monitorName, @Param("limit") int limit);
}
