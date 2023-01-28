package net.ludocrypt.frostyheights.util;

import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

public class QueueHashMap<K, V> extends LinkedList<Map.Entry<K, V>> {

	private static final long serialVersionUID = 7058282388303734950L;

	private final ConcurrentHashMap<K, V> delegate = new ConcurrentHashMap<K, V>();
	private final int maxSize;
	private final int chunkSize;

	public QueueHashMap(int maxSize, int chunkSize) {
		this.maxSize = maxSize;
		this.chunkSize = chunkSize;
	}

	public V get(K key) {
		return delegate.get(key);
	}

	public V getOrPut(K key, Supplier<V> supplier) {
		V value;
		if ((value = this.get(key)) != null) {
			return value;
		}
		return supplier.get();
	}

	@Override
	public boolean offer(Entry<K, V> e) {
		if (!this.contains(e)) {
			if (this.size() + 1 > maxSize) {
				ListIterator<Entry<K, V>> it = listIterator(0);
				for (int i = 0, n = chunkSize; i < n; i++) {
					this.delegate.remove(it.next());
					it.remove();
				}
			}
			delegate.put(e.getKey(), e.getValue());
			return super.offer(e);
		}
		return false;
	}

}
