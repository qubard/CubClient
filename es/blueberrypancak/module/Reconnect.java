package es.blueberrypancak.module;

import es.blueberrypancak.Client;
import es.blueberrypancak.event.EventDisconnect;
import es.blueberrypancak.event.EventRender;
import es.blueberrypancak.event.Subscribe;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.multiplayer.GuiConnecting;

@RegisterModule(color=0xFF9028)
public class Reconnect extends Module {

	private long lastReconnect;
	
	@Subscribe
	public void onRender(EventRender e) {
		Minecraft mc = Client.getMinecraft();
		if(lastReconnect == 0) lastReconnect = System.currentTimeMillis();
		String ip = mc.getCurrentServerData().serverIP;
		if(isEnabled() && ip != null && mc.theWorld == null && !(mc.currentScreen instanceof GuiConnecting)) {
			mc.fontRendererObj.drawStringWithShadow(getName(), 0, 0, getActiveColor());
			if(getElapsed() >= 5) {
				mc.displayGuiScreen(new GuiConnecting(new GuiMainMenu(), mc, ip, 25565));
				lastReconnect = System.currentTimeMillis();
			}
		}
	}
	
	@Subscribe
	public void onDisconnect(EventDisconnect e) {
		lastReconnect = System.currentTimeMillis();
	}
	
	private int getElapsed() {
		return (int)(System.currentTimeMillis()-lastReconnect)/1000;
	}
	
	@Override
	public void onEnabled() {
		
	}

	@Override
	public void onDisabled() {
		
	}

	@Override
	public String getName() {
		return getElapsed() + "s";
	}
}
