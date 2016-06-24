package es.blueberrypancak.module;

import es.blueberrypancak.Client;
import es.blueberrypancak.event.EventInWater;
import es.blueberrypancak.event.EventRender;
import es.blueberrypancak.event.Subscribe;

@RegisterModule(key=44,color=8372735,listed=true,pressed=true)
public class Sink extends Module {

	@Subscribe
	public void onInWater(EventInWater e) {
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
