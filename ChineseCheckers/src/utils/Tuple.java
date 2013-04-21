package utils;

public class Tuple<T, U> {
	
	private final T first;
	private final U second;
	
	public Tuple(T t, U u) {
		first  = t;
		second = u;
	}
	
	public T first() {
		return first;
	}
	
	public U second() {
		return second;
	}
}
