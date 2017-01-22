package priority.solving;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import priority.common.Constants;

public class Solution  implements Constants  {
	private static final String NEG = "!";
	Set<String> flow = new HashSet<String>();
	Set<String> priority = new HashSet<String>();
	Set<String> states = new HashSet<String>();
	Set<String> nextStates = new HashSet<String>();
	private Map<String, Boolean> nextStateValues = new HashMap<String, Boolean>();

	public Solution(String[] terms) {
		for (String term : terms) {
			String[] atoms = term.trim().split(" = ");
			if (atoms[0].trim().endsWith(TILDE))
				flow.add((atoms[1].trim().equals("0")?NEG:" ")+(atoms[0].trim()));
			else if (atoms[0].trim().endsWith(BULLET) || atoms[0].trim().endsWith(CIRC) && atoms[1].trim().equals("1"))
				priority.add(atoms[0].trim());
			else if (atoms[0].trim().endsWith(NEXT_MEMORY))
				nextStates.add((atoms[1].trim().equals("0")?NEG:" ")+(atoms[0].trim()));
			else if (atoms[0].trim().endsWith(CURRENT_MEMORY))
				states.add((atoms[1].trim().equals("0")?NEG:" ")+(atoms[0].trim()));
		}
	}
	
	public String toString() {
		return toString(false);
	}

	public String toString(boolean withPriority) {
		StringBuilder sb = new StringBuilder();
		for (String nodeflow : flow) {
			if (!nodeflow.trim().startsWith(NEG)) {
				if (nodeflow.trim().endsWith(TILDE))
					sb.append(" ").append(nodeflow.trim());
				if (priority.contains(nodeflow.trim().replaceFirst(TILDE, BULLET)) || priority.contains(nodeflow.trim().replaceFirst(TILDE, CIRC)))
					sb.append(" ").append(nodeflow.trim().replaceFirst(TILDE, "PRIORITY"));
			}
		}
		
		StringBuilder from = new StringBuilder();
		from.append(" [");
		for (String state : states) {
				if (state.trim().endsWith(CURRENT_MEMORY)) {
					from.append(state.trim()).append(' ');
				}
			}
		from.append("] ------ { ");
		
		StringBuilder to = new StringBuilder();
		to.append(" } -------> (");
		for (String state : nextStates) {
				if (state.trim().endsWith(NEXT_MEMORY)) {
					boolean neg = state.trim().startsWith(NEG);
					to.append(state).append(' ');
					String name = neg?state.trim().substring(1, state.trim().length()):state.trim();
					nextStateValues.put(name, !neg);
				}
		}
		to.append(") ");
	
		return from.append(sb.toString()).append(to.toString()).toString();
	}

	public Map<String, Boolean> nextStateValuess() {
		return nextStateValues;
	}
}