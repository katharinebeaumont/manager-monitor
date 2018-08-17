package agent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;
import org.springframework.web.client.RestTemplate;

import agent.loadmanager.ManagerAgent;
import agent.memory.ApplicationEntityService;
import agent.memory.LocationEntityService;
import agent.memory.MonitoringEntityService;
import agent.memory.domain.Application;
import agent.memory.domain.Location;
import agent.monitor.MonitoringAgent;

@SpringBootApplication
@EnableNeo4jRepositories("agent.memory")
@ComponentScan(basePackages = { "agent.*" } )
@EntityScan(basePackages = "agent.memory.domain")
public class AgentApplication {
	
	private static final Logger log = LoggerFactory.getLogger(AgentApplication.class);
	
	public static void main(String[] args) {
		for(String arg:args) {
            System.out.println(arg);
        }
		SpringApplication.run(AgentApplication.class, args);
	}
	
	@Autowired
	ManagerAgent manAgent;
	
	@Autowired
	MonitoringAgent monAgent;
	
	@Autowired
	ApplicationEntityService appService;
	
	@Autowired
	MonitoringEntityService slaveService;
	
	@Autowired
	LocationEntityService locService;
	
	
	@Value("${agent.mode}")
	private String agentMode;
	
	@Bean
    CommandLineRunner init(ApplicationContext appContext) {
		log.info("Initialising .... ");
		
		if (agentMode.equals("manager")) {
			
			log.info("Deleting all slaves");
			slaveService.deleteAll();
			
			log.info("Deleting all locations");
			locService.deleteAll();
			
			log.info("Deleting all applications");
			appService.deleteAll();
			Application entity = new Application("twitterClient");
			appService.save(entity);
			log.info("Saved new application twitterClient");
			
			Application entity2 = new Application("twitterService");
			appService.save(entity2);
			log.info("Saved new application twitterService");
			
			Location loc1 = new Location("http://localhost", "local", 8000);
			Location loc2 = new Location("http://localhost", "local", 9000);
			locService.save(loc1);
			log.info("Saved new location on localhost (port 8000)");
			locService.save(loc2);
			log.info("Saved new location on localhost (port 9000)");
			
			log.info("Staring in mode: manager");
			manAgent.startup();
		} else {
			log.info("Staring in mode: monitor");
			monAgent.startup();
		}
    		return null;
    }
	
	@Bean
	public RestTemplate restTemplate() {
	    return new RestTemplate();
	}
}