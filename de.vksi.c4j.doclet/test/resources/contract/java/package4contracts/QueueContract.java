package package4contracts;

import static de.vksi.c4j.Condition.postCondition;
import static de.vksi.c4j.Condition.preCondition;
import package4classes.Queue;
import de.vksi.c4j.Target;

public class QueueContract<T> extends Queue<T> {

	@Target
	private Queue<T> target;

	public QueueContract(int capacity) {
		super(capacity);
		if (preCondition()) {
			assert capacity > 0 : "capacity > 0";
		}
		if (postCondition()) {
			assert target.capacity() == capacity : "capacity set";
			assert target.size() == 0 : "size == 0";
		}
	}

}
