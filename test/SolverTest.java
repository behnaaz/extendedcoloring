package test;

import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import priority.init.ExampleMaker;
import priority.solving.IOAwareSolution;
import priority.solving.IOAwareStateValue;
import priority.solving.IOComponent;
import priority.solving.Solver;
import priority.states.StateValue;
import priority.states.StateVariableValue;

public class SolverTest {
	Optional<Boolean> optTrue = Optional.of(Boolean.TRUE);
	List<IOAwareSolution> s;
	Solver solver;
	
	@Before
	public void setUp() throws Exception {
		IOAwareStateValue currentStatesValue = new IOAwareStateValue(new StateValue(), new IOComponent("a", 1));
		ExampleMaker em = new ExampleMaker(3);
		solver = new Solver(em.getExample(currentStatesValue), currentStatesValue);
		s = solver.findSolutions(currentStatesValue, new ExampleMaker(3).getExample(currentStatesValue));
	}
	@Test
	@Ignore
	public void testFindSolutionFromBigState() throws Exception {
		IOAwareStateValue v = new IOAwareStateValue(new StateValue(), new IOComponent("a", 1));
		v.getStateValue().add(new StateVariableValue("de1de2ring", optTrue));
		v.getStateValue().add(new StateVariableValue("ij1ij2ring", optTrue));
		v.getStateValue().add(new StateVariableValue("jk1jk2ring", optTrue));

		s = solver.findSolutions(v, new ExampleMaker(3).getExample(v));
		assertEquals(s.size(), 4);

	}

	@Test
	public void testFindSolutions() throws Exception {
		assertEquals(s.size(), 4);

// [] ------ {  ab1tilde j2tilde jk1tilde } -------> (ab1ab2xringtrue jk1jk2xringtrue )  
		StateValue temp = new StateValue();
		temp.add(new StateVariableValue("ab1ab2ring", optTrue));
		temp.add(new StateVariableValue("jk1jk2ring", optTrue));
		assertEquals(temp, s.get(0).getSolution().getNextStateValue());
		assertEquals(0, s.get(0).getSolution().getFromVariables().size());
		assertEquals(2, s.get(0).getSolution().getToVariables().size());

// [] ------ {  ab1tilde } -------> (ab1ab2xringtrue )  
		temp = new StateValue();
		temp.add(new StateVariableValue("ab1ab2ring", optTrue));
		assertEquals(temp, s.get(1).getSolution().getNextStateValue());
		assertEquals(0, s.get(1).getSolution().getFromVariables().size());
		assertEquals(1, s.get(1).getSolution().getToVariables().size());

// [] ------ {  j2tilde jk1tilde } -------> (jk1jk2xringtrue )  
		temp = new StateValue();
		temp.add(new StateVariableValue("jk1jk2ring", optTrue));
		assertEquals(temp, s.get(2).getSolution().getNextStateValue());
		assertEquals(0, s.get(2).getSolution().getFromVariables().size());
		assertEquals(1, s.get(2).getSolution().getToVariables().size());

// [] ------ {  } -------> ()  
		temp = new StateValue();
		assertEquals(temp, s.get(3).getSolution().getNextStateValue());
		assertEquals(0, s.get(3).getSolution().getFromVariables().size());
		assertEquals(0, s.get(3).getSolution().getToVariables().size());
	}
	
	@Test
	public void testUpdateSolution() {
	//	List<IOAwareStateValue> explorableStates = new ArrayList<>();
	//	StateManager stateManager = new StateManager();
	//	List<IOAwareStateValue> visitedStates = new ArrayList<>();
//???TODO		List<IOAwareStateValue> t = solver.addToExplorableStates(visitedStates, explorableStates, stateManager, s);
		assertEquals(s.size(), 4);

		// [] ------ {  ab1tilde j2tilde jk1tilde } -------> (ab1ab2xringtrue jk1jk2xringtrue )  
		StateValue temp = new StateValue();
		temp.add(new StateVariableValue("ab1ab2ring", optTrue));
		temp.add(new StateVariableValue("jk1jk2ring", optTrue));
		assertEquals(temp, s.get(0).getSolution().getNextStateValue());
		assertEquals(0, s.get(0).getSolution().getFromVariables().size());
		assertEquals(2, s.get(0).getSolution().getToVariables().size());

		// [] ------ {  ab1tilde } -------> (ab1ab2xringtrue )  
				temp = new StateValue();
				temp.add(new StateVariableValue("ab1ab2ring", optTrue));
				assertEquals(temp, s.get(1).getSolution().getNextStateValue());
				assertEquals(0, s.get(1).getSolution().getFromVariables().size());
				assertEquals(1, s.get(1).getSolution().getToVariables().size());

		// [] ------ {  j2tilde jk1tilde } -------> (jk1jk2xringtrue )  
				temp = new StateValue();
				temp.add(new StateVariableValue("jk1jk2ring", optTrue));
				assertEquals(temp, s.get(2).getSolution().getNextStateValue());
				assertEquals(0, s.get(2).getSolution().getFromVariables().size());
				assertEquals(1, s.get(2).getSolution().getToVariables().size());

		// [] ------ {  } -------> ()  
				temp = new StateValue();
				assertEquals(temp, s.get(3).getSolution().getNextStateValue());
				assertEquals(0, s.get(3).getSolution().getFromVariables().size());
				assertEquals(0, s.get(3).getSolution().getToVariables().size());
	}
}
