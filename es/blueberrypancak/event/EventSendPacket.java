package es.blueberrypancak.event;

import net.minecraft.network.Packet;

public class EventSendPacket extends EventValue<Packet> {
	
	private boolean cancelled;
	
	public EventSendPacket(Packet value) {
		super(value);
	}

	public boolean isCancelled() {
		return cancelled;
	}
	
	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}
}
