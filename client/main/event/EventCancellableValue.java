package client.main.event;

public class EventCancellableValue<T> extends EventValue<T> implements EventCancellable {

	private boolean cancelled;
	
	public EventCancellableValue(T value) {
		super(value);
	}

	@Override
	public boolean isCancelled() {
		return this.cancelled;
	}

	@Override
	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}
}
