package agent;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

@Configuration
@ComponentScan(basePackages = { "agent.*" } )
@ImportResource("classpath:spring/spring-main.xml")
public class TestConfiguration {
	
}
