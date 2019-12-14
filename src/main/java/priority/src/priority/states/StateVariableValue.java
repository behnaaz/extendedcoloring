package priority.src.priority.states;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Optional;

import static priority.src.Variable.CURRENT_MEMORY;
import static priority.src.Variable.NEXT_MEMORY;

@Builder
@EqualsAndHashCode
@ToString
public class StateVariableValue implements Comparable<Object> {
	private String stateName;
	private Optional<Boolean> value;

	public String getStateName() {
		return stateName;
	}
	public void setStateName(String stateName) {
		this.stateName = stateName.trim().toLowerCase();
	}
	public Optional<Boolean> getValue() {
		return value;
	}
	
	public void setValue(Optional<Boolean> value) {
		this.value = value;
	}
	public String makeNextStateCurrent() {
		return getStateName().toLowerCase().replace(NEXT_MEMORY, CURRENT_MEMORY);
	}

	@Override
	public int compareTo(Object o) {
		if (this.equals(o))
			return 0;
		return 1;
	}
}