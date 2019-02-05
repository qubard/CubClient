package client.main.event;

public class EventChat extends EventCancellableValue<String> {

	public EventChat(String value) {
		super(value);
	}
}
