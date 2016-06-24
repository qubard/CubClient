package es.blueberrypancak.module;

import es.blueberrypancak.Client;
import es.blueberrypancak.event.EventRender;
import es.blueberrypancak.event.Subscribe;
import es.blueberrypancak.hook.EntityPlayerSPHook;

@RegisterModule(key=46,listed=true,color=16755200)
public class Fullbright extends Module {

	private float lastBrightness;
	
	@Subscribe
	public void onRender(EventRender e) {
		if(isEnabled()) {
			setBrightness(141);
		} else {
			setBrightness(lastBrightness);
		}
	}
	
	private void setBrightness(float f) {
		if(Client.getMinecraft().gameSettings.gammaSetting != f) { 
			Client.getMinecraft().gameSettings.gammaSetting = f;
		}
	}
	
	@Override
	public void onEnabled() {
		lastBrightness = Client.getMinecraft().gameSettings.gammaSetting;
	}

	@Override
	public void onDisabled() {
		setBrightness(lastBrightness);
	}

	@Override
	public String getName() {
		return "Fullbright";
	}
}
