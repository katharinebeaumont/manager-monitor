package agent;

import agent.manager.ManagerAgent;
import agent.memory.ApplicationEntityService;
import agent.memory.LocationEntityService;
import agent.memory.MonitoringEntityService;
import agent.memory.domain.Application;
import agent.memory.domain.Location;
import agent.monitor.MonitoringAgent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.ApplicationPidFileWriter;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;
import org.springframework.web.client.RestTemplate;

@Configuration
@EnableAutoConfiguration
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
		SpringApplication app = new SpringApplication(AgentApplication.class);
		app.addListeners(new ApplicationPidFileWriter());
		app.run(args);
	}

	@Autowired
	ManagerAgent manAgent;
	
	@Autowired
	MonitoringAgent monAgent;
	
	@Autowired
	ApplicationEntityService appService;
	
	@Autowired
	MonitoringEntityService monitorService;
	
	@Autowired
	LocationEntityService locService;
	
	@Value("${agent.mode}")
	private String agentMode;
	
	@Value("${experiment}")
	private int experiment;
	
	@Bean
    CommandLineRunner init(ApplicationContext appContext) {
		log.info("Initialising .... ");
		
		if (agentMode.equals("manager")) {
			if (experiment == 2) {
				log.info("Starting experiment 1: automatically deploy agent and mock service locally.");
				log.info("Deleting all monitors");
				monitorService.deleteAll();
				
				log.info("Deleting all locations");
				locService.deleteAll();
				
				log.info("Deleting all applications");
				appService.deleteAll();
				
				Application entity1 = new Application("mockService", "mockService.jar");
				appService.save(entity1);
				log.info("Saved new application " + entity1.getName());
				
				log.info("Creating 2 possible locations");
				Location loc1 = new Location("http://localhost", "local", 3000, false);
				locService.save(loc1);
				Location loc2 = new Location("http://localhost", "local", 7000, false);
				locService.save(loc2);
			}
			
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