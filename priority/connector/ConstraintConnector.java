package priority.connector;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.base.Function;
import com.google.common.base.Strings;

import priority.common.Constants;

public class ConstraintConnector extends AbstractConnector implements Constants {
	static final String FORMULA_NAME = "qaz";
	private static final String WORD_BOUNDARY = "\\b";
	private String constraint;
	OutputStreamWriter out;
	ConnectorFactory cf = new ConnectorFactory();
	private String[] states;
	private String[] nextStates;

	public ConstraintConnector(String constraint, String... names) {
		super(names);
		this.constraint = constraint;
	}
	
	public Map<String, Boolean> initStateValues() {
		Map<String, Boolean> result = new HashMap<>();
		for (String state : states) {
			result.put(state.toLowerCase().replace("ring", "xring"), false);
		}
		return result;
	}
	
	public String getConstraint() {
		return constraint;
	}

	public Set<String> variables() {
		return variables(constraint);
	}

	private Set<String> variables(String constraint) {
		if (Strings.isNullOrEmpty(constraint))
			return Collections.emptySet();

		Set<String> result = new HashSet<>();
		String copy = constraint.replaceAll(AND.trim()+"|"+IMPLIES.trim()+"|"+NOT.trim()+"|\\(|\\)|,|"+RIGHTLEFTARROW.trim()+"|"+OR.trim()+"|"+TRUE.trim()+"|"+FALSE.trim(), "");
		//constraint
		for (String s : copy.split(" ")) {
			if (s.trim().length() > 0) {
				result.add(s.toUpperCase());
				this.constraint = this.constraint.replaceAll(new StringBuilder().append(WORD_BOUNDARY).append(s).append(WORD_BOUNDARY).toString(), s.toUpperCase());//TODO
			}
		}
		return result;
	}
	
	void printVariables(Function<String, Set<String>> ff) throws IOException {
		out.write(ff.apply(this.constraint).toString());
	}

	void printVariables(String formulae) throws IOException {
		Set<String> vars = this.variables(formulae).stream().filter(e -> e.length()>0).map(String::toUpperCase).collect(Collectors.toSet());
		out.write("rlpcvar "+vars.toString().substring(1, vars.toString().length()-1)+";");
	}
	
	public void output(OutputStreamWriter out) {
		this.out = out;
		try {
			preamble();
			printVariables(constraint);
			out.write(FORMULA_NAME + " := " + constraint+";;");
			dnf(FORMULA_NAME);
			out.write("shut; end;");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void dnf(String formulae) throws IOException {
		out.write("rldnf " + formulae +";");
	}

	private void preamble() throws IOException {
		out.write("set_bndstk_size 100000;");
		out.write("load_package \"redlog\";");
		out.write("rlset ibalp;");
	}

	public void add(ConstraintConnector newConnector, String p1, String p2) {
		if (p1 != null && p1.length() > 0)
			names.add(p1);
		
		constraint = String.format("%s " + AND + " %s" + AND + 
				"( %s" + RIGHTLEFTARROW + " %s )"// + AND +
				//"(" + NEG + "("+ "))"
				,
				constraint, newConnector.getConstraint(), cf.flow(p1), cf.flow(p2));
				
	}
	
	public void add(ConstraintConnector newConnector, String p1, String p2, boolean use) {
		if (p1 != null && p1.length() > 0)
			names.add(p1);
		ConnectorFactory factory = new ConnectorFactory();
		constraint = String.format("%s " + AND + " %s" + AND + 
				"( %s" + RIGHTLEFTARROW + " %s)"// + AND +
				//"(" + NEG + "("+ "))"
				,
				constraint, newConnector.getConstraint(), factory.flow(p1), factory.flow(p2));
				
	}
//???TODO
	public ConstraintConnector connect(String p1, String p2) {
		return new ConstraintConnector(String.format(" (%s %s %s) ", p1, IMPLIES, p2));
	}

	public void close() throws IOException {
		out.flush();
		out.close();
	}

	public void setStates(String... mems) {
		states = mems;
	}
	
	public void setNextStates(String... nexts) {
		nextStates = nexts;
	}
	
	public String[] getStates() {
		if (states == null)
			return new String[0];
		return states;
	}
	
	public String[] getNextStates() {
		if (nextStates == null)
			return new String[0];//TODO???
		return nextStates;
	}
}