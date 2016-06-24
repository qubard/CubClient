package es.blueberrypancak.event;

public class EventValue<T> implements Event {
	
	private T value;
	
	public EventValue(T value) {
		this.value = value;
	}
	
	public T getValue() {
		return this.value;
	}
	
	public void setValue(T value) {
		this.value = value;
	}
}
