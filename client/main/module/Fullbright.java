package client.main.module;

import client.main.Client;
import client.main.event.EventRender;
import client.main.event.Subscribe;

@RegisterModule(key = 46, listed = true, color = 16755200)
public class Fullbright extends Module {

	private float lastBrightness;

	@Subscribe
	public void onRender(EventRender e) {
		setBrightness(isEnabled() ? 141 : lastBrightness);
	}

	private void setBrightness(float f) {
		if (Client.getMinecraft().gameSettings.gammaSetting != f) {
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
