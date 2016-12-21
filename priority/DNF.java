package priority;

import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import static priority.Constraint.OR;
import static priority.Constraint.AND;

public class DNF {
	private String filePath;
	private List<String> variables;
	private Set<Solution> solutions = new HashSet<Solution>();
	private List<String> states;
	private List<String> nextStates;

	public void printFlows() {
		System.out.println("Flows~~~~~~~~~~~");
		for (Solution sol : solutions) {
			System.out.println(sol.toString());
		}
	}

	public List<String> getVariables() {
		return variables;
	}

	public DNF(String file, List<String> variables, List<String> states, List<String> nextStates) {
		this.filePath = file;
		this.variables = variables;
		this.states = states;
		this.nextStates = nextStates;
	}

	public void reportVars() {
		for (String v : variables)
			System.out.println(variables.indexOf(v.trim().toUpperCase()) + " " + v);
	}
	
	public void prepareForSat4j(Writer writer) throws Exception {
		StringBuilder sb = new StringBuilder();
		for (String line : Files.lines(Paths.get(filePath)).filter(s -> s.length() > 0).map(s -> s).collect(Collectors.toList())) {
		//	System.out.println(line+"..");
			sb.append(line);
		}
		writer.write("c test\r\n");
		writer.write("c\r\n");
		
		String constraint = sb.toString();
		if (constraint.trim().equalsIgnoreCase("false"))
			throw new Exception("NOT FEASIBLE");

		assert(constraint.contains(OR.trim()));//OTHERWISE not supported yet
		String[] ands = constraint.split(OR.trim());
		writer.write("p cnf " + variables.size() + " " + ands.length + "\r\n");

		for (String and : ands) {
			sb = new StringBuilder();
			String terms[] = and.trim().substring(1, and.trim().length()-1).split(AND.trim());
			for (String term : terms) {
				String[] atoms = term.trim().split(" = ");
				if (variables.indexOf(atoms[0].toUpperCase()) <= -1) 
					throw new Exception(atoms[0]+" not found");
				sb.append((atoms[1].trim().equals("0")?"-":"")+(variables.indexOf(atoms[0].toUpperCase())+1)+" ");
			}
			writer.write(sb.toString() + " 0\r\n");

			//System.out.println(and.trim().substring(1, and.trim().length()-1));
		}
		writer.close();
	}

	public void reportSolutions() throws IOException {
		System.out.println("====================");
		StringBuilder sb = new StringBuilder();
		for (String line : Files.lines(Paths.get(filePath)).filter(s -> s.length() > 0).map(s -> s).collect(Collectors.toList())) {
		//	System.out.println(line+"..");
			sb.append(line);
		}

		String[] ands = sb.toString().split(OR.trim());

		for (String and : ands) {
			sb = new StringBuilder();
			String terms[] = and.trim().substring(1, and.trim().length()-1).split(AND.trim());
			
			for (String term : terms) {
				String[] atoms = term.trim().split(" = ");
			//	if (variables.indexOf(atoms[0].toUpperCase()) <= -1) 
				//	throw new Exception(atoms[0]+" not found");
				sb.append((atoms[1].trim().equals("0")?"!":" ")+(atoms[0].trim())+" ");
			}
			Solution newSol = new Solution(terms);
			if (!redundantSolution(solutions, newSol))
				solutions.add(newSol);

			System.out.println(sb.toString() + " \r\n");
		}		
	}

	private boolean redundantSolution(Set<Solution> solutions2, Solution newSol) {
		for (Solution sol : solutions) {
			if (sol.toString().equals(newSol.toString()))
				return true;
		}
		return false;
	}

	public void printFlowsNPriority() {
		System.out.println("Flows~~~~~~~~~~~");
		for (Solution sol : solutions) {
			System.out.println(sol.toString(true));
		}
	}

	public List<Map<String, Boolean>> stateValues() {
		List<Map<String, Boolean>> nexts = new ArrayList<Map<String, Boolean>>();
		System.out.println("States~~~~~~~~~~~");
		for (Solution sol : solutions) {
			System.out.println(sol.nextStateValuess().toString());
			Map<String, Boolean> t = Constraint.makeItCurrent(sol.nextStateValuess());
			if (!nexts.contains(t))
				nexts.add(t);
		}
		return nexts;
	}
}