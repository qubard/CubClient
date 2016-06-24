package es.blueberrypancak.event;

public class EventInWater implements Event {
	
	private boolean inWater;
	
	public EventInWater(boolean inWater) {
		this.inWater = inWater;
	}
	
	public void setValue(boolean inWater) {
		this.inWater = inWater;
	}
	
	public boolean getValue() {
		return this.inWater;
	}
}
