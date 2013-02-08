package internalContract;

import internalContract.StackContract;
import de.vksi.c4j.ContractReference;

@ContractReference(StackContract.class)
public class Stack<T> implements StackSpec<T> {

	private Object[] values;
	private int capacity;
	private int size;

	public Stack(int capacity) {
		values = new Object[capacity];
		this.capacity = capacity;
		size = 0;
	}

	@Override
	public void push(T item) {
		values[size] = item;
		size = size + 1;
	}

	@Override
	public void pop() {
		values[size] = null;
		size = size - 1;
	}

	@Override
	@SuppressWarnings("unchecked")
	public T top() {
		T result = null;
		result = (T) values[size - 1];
		return result;
	}

	@Override
	public int size() {
		int result = 0;
		result = size;
		return result;
	}

	@Override
	public boolean isEmpty() {
		boolean result = false;
		if (size() == 0) {
			result = true;
		}
		return result;
	}

	@Override
	public boolean isFull() {
		boolean result = false;
		result = size == capacity;
		return result;
	}

	@Override
	public int capacity() {
		int result = 0;
		result = capacity;
		return result;
	}

}
