package package4classes;

import package4contracts.QueueSpecContract;
import de.vksi.c4j.ContractReference;
import de.vksi.c4j.Pure;

@ContractReference(QueueSpecContract.class)
public interface QueueSpec<T> {

	// basic queries

	@Pure
	int capacity();

	@Pure
	int size();

	@Pure
	T get(int index);

	@Pure
	QueueSpec<T> shallowCopy();

	// derived queries

	@Pure
	T head();

	@Pure
	boolean isEmpty();

	@Pure
	boolean isFull();

	// commands

	void put(T item);

	void remove();

}
