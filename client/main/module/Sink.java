package client.main.module;

import client.main.event.EventInLava;
import client.main.event.EventInWater;
import client.main.event.Subscribe;

@RegisterModule(key = 44, color = 8372735, listed = true, pressed = true)
public class Sink extends Module {

	@Subscribe
	public void onInWater(EventInWater e) {
		e.setValue(isEnabled() ? false : e.getValue());
	}

	@Subscribe
	public void isInLava(EventInLava e) {
		e.setValue(isEnabled() ? false : e.getValue());
	}

	@Override
	public void onEnabled() {

	}

	@Override
	public void onDisabled() {

	}

	@Override
	public String getName() {
		return "Sink";
	}
}
