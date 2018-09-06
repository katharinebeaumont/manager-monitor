package agent.manager.learning;

import static org.junit.Assert.*;

import org.junit.Test;

public class StateTest {

	@Test
	public void testEqualsWhenSameElementsInArray() {
		//Want 2 States with same values to have the same hashcode
		String[] desc1 = new String[] {"test"};
		String[] desc2 = new String[] {"test"};
		State s1 = new State(desc1);
		State s2 = new State(desc2);
		
		
		//Assert
		assertTrue(s1.equals(s2));
	}

	@Test
	public void testEqualsWhenSameArray() {
		//Want 2 States with same values to have the same hashcode
		String[] desc1 = new String[] {"test"};
		State s1 = new State(desc1);
		State s2 = new State(desc1);
		
		//Assert
		assertTrue(s1.equals(s2));
	}
	
	@Test
	public void testNotEqualsWhenArrayLengthsDiffer() {
		//Want 2 States with same values to have the same hashcode
		String[] desc1 = new String[] {"test"};
		String[] desc2 = new String[] {"test","balloon"};
		State s1 = new State(desc1);
		State s2 = new State(desc2);
		
		
		//Assert
		assertTrue(!s1.equals(s2));
	}

	@Test
	public void testNotEqualsWhenArrayContentsDiffer() {
		//Want 2 States with same values to have the same hashcode
		String[] desc1 = new String[] {"test"};
		String[] desc2 = new String[] {"tset"};
		State s1 = new State(desc1);
		State s2 = new State(desc2);
		
		//Assert
		assertTrue(!s1.equals(s2));
	}

	@Test
	public void testHashCodeEquals() {
		//Want 2 States with same values to have the same hashcode
		String[] desc1 = new String[] {"test"};
		String[] desc2 = new String[] {"test"};
		State s1 = new State(desc1);
		State s2 = new State(desc2);
		
		//Act
		int hash1 = s1.hashCode();
		int hash2 = s2.hashCode();
		
		//Assert
		assertEquals(hash1, hash2);
	}
	
	@Test
	public void testHashCodeEqualsMultipleArrayElements() {
		//Want 2 States with same values to have the same hashcode
		String[] desc1 = new String[] {"test","location1"};
		String[] desc2 = new String[] {"test","location1"};
		State s1 = new State(desc1);
		State s2 = new State(desc2);
		
		//Act
		int hash1 = s1.hashCode();
		int hash2 = s2.hashCode();
		
		//Assert
		assertEquals(hash1, hash2);
	}
	
	@Test
	public void testHashCodeNotEquals() {
		//Want 2 States with different values to have different hashcodes
		String[] desc1 = new String[] {"test"};
		String[] desc2 = new String[] {"test2"};
		State s1 = new State(desc1);
		State s2 = new State(desc2);
		
		//Act
		int hash1 = s1.hashCode();
		int hash2 = s2.hashCode();
		
		//Assert
		assertTrue(hash1 != hash2);
	}
}
