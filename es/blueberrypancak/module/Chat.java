package es.blueberrypancak.module;

import es.blueberrypancak.event.EventDrawChat;
import es.blueberrypancak.event.Subscribe;

@RegisterModule(key = 52)
public class Chat extends Module {

	@Subscribe
	public void onDrawChat(EventDrawChat e) {
		if (isEnabled()) {
			e.setCancelled(true);
		}
	}

	@Override
	public void onEnabled() {

	}

	@Override
	public void onDisabled() {

	}

	@Override
	public String getName() {
		return null;
	}
}
