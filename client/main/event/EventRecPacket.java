package client.main.event;

import net.minecraft.network.Packet;

public class EventRecPacket extends EventCancellableValue<Packet>{

	public EventRecPacket(Packet value) {
		super(value);
	}
}
