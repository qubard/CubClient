package es.blueberrypancak.event;

public class EventIsSneaking implements Event {

	private boolean isSneaking;
	
	public EventIsSneaking(boolean isSneaking) {
		this.isSneaking = isSneaking;
	}
	
	public void setValue(boolean isSneaking) {
		this.isSneaking = isSneaking;
	}
	
	public boolean getValue() {
		return this.isSneaking;
	}
}
