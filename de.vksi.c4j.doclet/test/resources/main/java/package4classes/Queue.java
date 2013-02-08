package package4classes;

import package4contracts.QueueContract;
import de.vksi.c4j.ContractReference;

@ContractReference(QueueContract.class)
public class Queue<T> implements QueueSpec<T> {

	private final int capacity;
	private int size;
	private Object[] values;

	public Queue(int capacity) {
		this.capacity = capacity;
		values = new Object[capacity];
	}

	@Override
	public int capacity() {
		return capacity;
	}

	@Override
	public int size() {
		return size;
	}

	@Override
	@SuppressWarnings("unchecked")
	public T get(int index) {
		T result = null;
		result = (T) values[index];
		return result;
	}

	@Override
	@SuppressWarnings("unchecked")
	public T head() {
		T result = null;
		result = (T) values[0];
		return result;
	}

	@Override
	public boolean isEmpty() {
		boolean result = false;
		result = size == 0;
		return result;
	}

	@Override
	public boolean isFull() {
		boolean result = false;
		result = size == capacity;
		return result;
	}

	@Override
	public QueueSpec<T> shallowCopy() {
		QueueSpec<T> result = null;
		result = new Queue<T>(capacity);
		for (int i = 0; i < size; i = i + 1) {
			result.put(get(i));
		}
		return result;
	}

	@Override
	public void put(T item) {
		values[size] = item;
		size = size + 1;
	}

	@Override
	public void remove() {
		size = size - 1;
		for (int i = 0; i < size; i = i + 1) {
			values[i] = values[i + 1];
		}
		values[size] = null;
	}

}
