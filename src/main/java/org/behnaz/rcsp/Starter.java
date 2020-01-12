package org.behnaz.rcsp;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class Starter {
	
	public static void main(String[] args) {
		final IOAwareStateValue initState = new IOAwareStateValue(makeInitState("a1b1ring"), null/*new IOComponent("w1", 10000000)*/);
		final ConstraintConnector cc = new ExampleMaker(1).getExample(initState);
		List<IOAwareSolution> solutions = null;
		try {
			solutions = Solver.builder()
					.connectorConstraint(cc)
					.initState(initState)
					.build()
					.solve(-1);
		} catch (IOException e) {
			log("Solving constraints with reduce failed " +e.getMessage());
		}
		//System.out.println("Use new method?" + Solver.USE_EQUAL_SET_ON);
      	//  final Drawer d = new Drawer(solutions);
		//  d.draw();
        //	d.toGoJS();
	}

	public static void log(String s) {
		System.out.println(s);
	}

	private static StateValue makeInitState(final String stateName) {
		return StateValue.builder()
				.variableValues(
						Collections.singleton(
								StateVariableValue.builder()
										.stateName(stateName)
										.value(Optional.of(Boolean.TRUE))
										.build()
						)
				)
				.build();
	}
}