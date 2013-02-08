package package4contracts;

import static de.vksi.c4j.Condition.ignored;
import static de.vksi.c4j.Condition.old;
import static de.vksi.c4j.Condition.postCondition;
import static de.vksi.c4j.Condition.preCondition;
import static de.vksi.c4j.Condition.result;
import static de.vksi.c4j.Condition.unchanged;
import package4classes.QueueSpec;
import de.vksi.c4j.ClassInvariant;
import de.vksi.c4j.Target;

public class QueueSpecContract<T> implements QueueSpec<T> {

	@Target
	private QueueSpec<T> target;

	@ClassInvariant
	public void classInvariant() {
		assert unchanged(target.capacity()) : "capacity is constant";
		assert target.capacity() > 0 : "capacity > 0";
		assert target.size() >= 0 : "size >= 0";
		assert target.size() <= target.capacity() : "size <= capacity";
	}

	@Override
	public int capacity() {
		return ignored();
	}

	@Override
	public int size() {
		return ignored();
	}

	@Override
	public T get(int index) {
		if (preCondition()) {
			assert !target.isEmpty() : "not isEmpty";
			assert index >= 0 : "index >= 0";
			assert index < target.size() : "index < size";
		}
		if (postCondition()) {
			T result = result();
			assert result != null : "result != null";
		}
		return ignored();
	}

	@Override
	public T head() {
		if (preCondition()) {
			assert !target.isEmpty() : "not isEmpty";
		}
		if (postCondition()) {
			T result = result();
			assert result == target.get(0) : "result == get(0)";
		}
		return ignored();
	}

	@Override
	public boolean isEmpty() {
		if (postCondition()) {
			boolean result = result();
			if (result) {
				assert target.size() == 0 : "if isEmpty then size == 0";
			}
		}
		return ignored();
	}

	@Override
	public boolean isFull() {
		if (postCondition()) {
			boolean result = result();
			if (result) {
				assert target.size() == target.capacity() : "if isFull then size == capacity";
			}
		}
		return ignored();
	}

	@Override
	public QueueSpec<T> shallowCopy() {
		if (postCondition()) {
			QueueSpec<T> result = result();
			assert result.size() == target.size() : "same size";
			for (int i = 0; i < target.size(); i = i + 1) {
				assert result.get(i) == target.get(i) : "same content";
			}
		}
		return ignored();
	}

	@Override
	public void put(T item) {
		if (preCondition()) {
			assert item != null : "item != null";
			assert !target.isFull() : "not isFull";
		}
		if (postCondition()) {
			assert target.size() == old(target.size()) + 1 : "size = old size + 1";
			assert target.get(target.size() - 1) == item : "item set at index size - 1";
		}
	}

	@Override
	public void remove() {
		if (preCondition()) {
			assert !target.isEmpty() : "not isEmpty";
		}
		if (postCondition()) {
			assert target.size() == old(target.size()) - 1 : "size = old size - 1";
			for (int i = 0; i < target.size(); i = i + 1) {
				assert target.get(i) == old(target.shallowCopy()).get(i + 1) : "get(i) == old get(i + 1)";
			}
		}
	}
}
