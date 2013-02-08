package internalContract;

import static de.vksi.c4j.Condition.postCondition;
import static de.vksi.c4j.Condition.preCondition;
import internalContract.Stack;
import de.vksi.c4j.Target;

public class StackContract<T> extends Stack<T> {

	@Target
	private Stack<T> target;

	public StackContract(int capacity) {
		super(capacity);
		if (preCondition()) {
			assert capacity > 0 : "capacity > 0";
		}
		if (postCondition()) {
			assert target.capacity() == capacity : "capacity set";
		}
	}

}
