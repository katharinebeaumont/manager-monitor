package agent.manager.learning;

import static org.junit.Assert.*;

import org.junit.Test;

public class QTableTest {

	double alpha = 0.5;
	double gamma = 0.7;
	
	@Test
	public void testAdd() {
		//Arrange
		QTable qTable = new QTable(alpha, gamma, "bob");
		int R = 10;
		State S = new State(new String[] {"State1"});
		Action A = new Action(ActionEnum.DO_NOTHING);
		State S_next = new State(new String[] {"State2"});
		
		//Q(S,A) + alpha[R + (gamma * max Q(S',a) - Q(S,A)]
		// = 0 + 0.5(10 + (0.7*0) - 0)
		// = 5
		double expectedResult = 5.0;
				
		//Act
		qTable.add(S, S_next, A, R);
		Double result = qTable.getValue(S, A);
		
		//Assert
		assertEquals(expectedResult, result, 0.0001);
		
	}
	
	@Test
	public void testAddMultipleSteps() {
		//Arrange
		QTable qTable = new QTable(alpha, gamma, "bob");
		int R = 10;
		State S = new State(new String[] {"State1"});
		Action A = new Action(ActionEnum.DO_NOTHING);
		State S_next = new State(new String[] {"State2"});
		
		//Q(S,A) + alpha[R + (gamma * max Q(S',a) - Q(S,A)]
		// = 0 + 0.5(10 + (0.7*0) - 0)
		// = 5
		qTable.add(S, S_next, A, R);
		Double result = qTable.getValue(S, A);
		assertEquals(5.0, result, 0.0001);
		
		//Now add another value
		int R_next = -2;
		State S_next_next = new State(new String[] {"State3"});
		Action A_next = new Action(ActionEnum.DUPLICATE_AND_DEPLOY);
		qTable.add(S_next, S_next_next, A_next, R_next);
		//Q(S_next,A_next) + alpha[R_next + (gamma * max Q(S_next_next',a) - Q(S_next,A_next)]
		// = 0 + 0.5(-2 + (0.7*0) - 0)
		// = -1
		result = qTable.getValue(S_next, A_next);
		
		//Assert
		assertEquals(-1.0, result, 0.0001);
		
		//Go back and add another reward in for the first State and Action
		qTable.add(S, S_next, A, R);
		result = qTable.getValue(S, A);
		//Q(S,A) + alpha[R + (gamma * max Q(S',a) - Q(S,A)]
		// = 5 + 0.5(10 + (0.7*0) - 5)
		// = 9.25
		assertEquals(7.5, result, 0.0001);
		
	}
	
	@Test
	public void testFindBestActionForStates() {
		QTable qTable = new QTable(alpha, gamma, "bob");
		int R = 10;
		State S = new State(new String[] {"State1"});
		Action A = new Action(ActionEnum.DO_NOTHING);
		State S_next = new State(new String[] {"State2"});
		
		Action A_2 = new Action(ActionEnum.DUPLICATE_AND_DEPLOY);
		int R_2 = -1;
		
		//Act
		qTable.add(S, S_next, A, R);
		qTable.add(S, S_next, A_2, R_2);
		
		Action bestAction = qTable.findBestActionForState(S);
		
		//Assert
		assertEquals(new Action(ActionEnum.DO_NOTHING), bestAction);
		
	}
	
	@Test
	public void testFindBestActionForStatesWhenNoBestAction() {
		QTable qTable = new QTable(alpha, gamma, "bob");
		int R = -1;
		State S = new State(new String[] {"State1"});
		Action A = new Action(ActionEnum.DO_NOTHING);
		State S_next = new State(new String[] {"State2"});
		
		Action A_2 = new Action(ActionEnum.DUPLICATE_AND_DEPLOY);
		int R_2 = -1;
		
		//Act
		qTable.add(S, S_next, A, R);
		qTable.add(S, S_next, A_2, R_2);
		
		Action bestAction = qTable.findBestActionForState(S);
		
		//Assert
		assertNull(bestAction);
		
	}
}
