package client.main.event;

import net.minecraft.entity.Entity;

public class EventAttack extends EventCancellableValue<Entity> {

	public EventAttack(Entity value) {
		super(value);
	}
}
