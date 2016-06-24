package es.blueberrypancak.event;

import net.minecraft.network.Packet;

public class EventSendPacket implements Event{
	
	private Packet packet;
	
	private boolean cancelled;
	
	public EventSendPacket(Packet packet) {
		this.packet = packet;
	}
	
	public void setValue(Packet packet) {
		this.packet = packet;
	}
	
	public boolean isCancelled() {
		return cancelled;
	}
	
	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}
	
	public Packet getValue() {
		return this.packet;
	}
	
}
