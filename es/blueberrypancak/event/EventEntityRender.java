package es.blueberrypancak.event;

public class EventEntityRender implements Event {

	private float partialTicks;
	
	public EventEntityRender(float partialTicks) {
		this.partialTicks = partialTicks;
	}
	
	public float getTicks() {
		return partialTicks;
	}
}
