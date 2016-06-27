package es.blueberrypancak.event;

public class EventChat extends EventCancellableValue<String> {

	public EventChat(String value) {
		super(value);
	}
}
