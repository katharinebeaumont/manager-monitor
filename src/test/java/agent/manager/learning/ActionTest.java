package agent.manager.learning;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import agent.memory.domain.Location;

public class ActionTest {
	
	@Test
	public void testEqualsWhenNullLocation() {
		Action a1 = new Action(ActionEnum.DO_NOTHING);
		Action a2 = new Action(ActionEnum.DO_NOTHING);
		
		//Assert
		assertTrue(a1.equals(a2));
	}

	@Test
	public void testEqualsWhenSameArrayLocationAndAction() {
		Location loc = new Location("path", "local", 8080);
		Location loc2 = new Location("path", "local", 8080);
		Action a1 = new Action(ActionEnum.DO_NOTHING, loc);
		Action a2 = new Action(ActionEnum.DO_NOTHING, loc2);
		
		//Assert
		assertTrue(a1.equals(a2));
	}
	
	@Test
	public void testNotEqualsWhenLocationsDiffer() {
		Location loc = new Location("path", "local", 8080);
		Location loc2 = new Location("path", "remote", 8080);
		Action a1 = new Action(ActionEnum.DO_NOTHING, loc);
		Action a2 = new Action(ActionEnum.DO_NOTHING, loc2);
		
		//Assert
		assertTrue(!a1.equals(a2));
	}

	@Test
	public void testNotEqualsWhenActionsDiffer() {
		Location loc = new Location("path", "local", 8080);
		Location loc2 = new Location("path", "local", 8080);
		Action a1 = new Action(ActionEnum.DUPLICATE_AND_DEPLOY, loc);
		Action a2 = new Action(ActionEnum.DO_NOTHING, loc2);
		
		//Assert
		assertTrue(!a1.equals(a2));
	}

	@Test
	public void testHashCodeEquals() {
		Action a1 = new Action(ActionEnum.DO_NOTHING);
		Action a2 = new Action(ActionEnum.DO_NOTHING);
		
		//Act
		int hash1 = a1.hashCode();
		int hash2 = a2.hashCode();
		
		//Assert
		assertEquals(hash1, hash2);
	}
	
	@Test
	public void testHashCodeEqualsLocationAndActionSame() {
		Location loc = new Location("path", "local", 8080);
		Location loc2 = new Location("path", "local", 8080);
		Action a1 = new Action(ActionEnum.DO_NOTHING, loc);
		Action a2 = new Action(ActionEnum.DO_NOTHING, loc2);
		
		//Act
		int hash1 = a1.hashCode();
		int hash2 = a2.hashCode();
		
		//Assert
		assertEquals(hash1, hash2);
	}
	
	@Test
	public void testHashCodeNotEquals() {
		//Want 2 States with different values to have different hashcodes
		Action a1 = new Action(ActionEnum.DO_NOTHING);
		Action a2 = new Action(ActionEnum.DUPLICATE_AND_DEPLOY);
		
		//Act
		int hash1 = a1.hashCode();
		int hash2 = a2.hashCode();
		
		//Assert
		assertTrue(hash1 != hash2);
	}
}
