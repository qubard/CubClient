package client.main.event;

import client.main.Location;

public class EventLoadBlock extends EventValue<Location> {

	public EventLoadBlock(Location loc) {
		super(loc);
	}
}
