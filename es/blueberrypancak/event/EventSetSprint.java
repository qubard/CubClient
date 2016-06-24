package es.blueberrypancak.event;

public class EventSetSprint implements Event {
	
	private boolean isSprinting;
	
	public EventSetSprint(boolean isSprinting) {
		this.isSprinting = isSprinting;
	}
	
	public void setValue(boolean isSprinting) {
		this.isSprinting = isSprinting;
	}
	
	public boolean getValue() {
		return this.isSprinting;
	}
}
