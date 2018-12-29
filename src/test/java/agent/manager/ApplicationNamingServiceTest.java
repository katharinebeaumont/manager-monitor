package agent.manager;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class ApplicationNamingServiceTest {

	private ApplicationNamingService appNamingService;
	
	@Before
	public void before() {
		appNamingService = new ApplicationNamingService();
	}
		
	@Test
	public void testNamingServiceNoVersionNumber() {
		String testStr = "monitor";
		
		String result = appNamingService.generateName(testStr);
		
		assertEquals("monitor2", result);
	}
	
	@Test
	public void testNamingServiceVersionNumber() {
		String testStr = "monitor4";
		
		String result = appNamingService.generateName(testStr);
		
		assertEquals("monitor5", result);
	}
	
	@Test
	public void testNamingServiceVersionNumberDoubleFigures() {
		String testStr = "monitor34";
		
		String result = appNamingService.generateName(testStr);
		
		assertEquals("monitor35", result);
	}
	
	@Test
	public void testNamingServiceVersionNumberDoubleFiguresAgain() {
		String testStr = "monitor39";
		
		String result = appNamingService.generateName(testStr);
		
		assertEquals("monitor40", result);
	}
}
