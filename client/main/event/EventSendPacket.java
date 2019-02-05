package client.main.event;

import net.minecraft.network.Packet;

public class EventSendPacket extends EventCancellableValue<Packet> {
	
	public EventSendPacket(Packet value) {
		super(value);
	}
}
