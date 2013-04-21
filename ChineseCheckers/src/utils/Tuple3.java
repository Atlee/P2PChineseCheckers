package utils;

public class Tuple3<T, U, V> {
	
	private final T first;
	private final U second;
	private final V third;
	
	public Tuple3(T t, U u, V v) {
		first = t;
		second = u;
		third = v;
	}

	public T getFirst() {
		return first;
	}
	
	public U getSecond() {
		return second;
	}
	
	public V getThird() {
		return third;
	}
}
