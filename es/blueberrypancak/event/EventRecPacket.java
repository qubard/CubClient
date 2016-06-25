package es.blueberrypancak.event;

import net.minecraft.network.Packet;

public class EventRecPacket extends EventValue<Packet> {

	private boolean cancelled;

	public EventRecPacket(Packet value) {
		super(value);
	}

	public boolean isCancelled() {
		return cancelled;
	}

	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}
}
