package es.blueberrypancak.module;

import es.blueberrypancak.Client;
import es.blueberrypancak.event.EventDisconnect;
import es.blueberrypancak.event.EventRender;
import es.blueberrypancak.event.Subscribe;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.multiplayer.GuiConnecting;
import net.minecraft.client.multiplayer.ServerData;

@RegisterModule(color=0xFF9028)
public class Reconnect extends Module {

	private String lastIp;
	
	private long lastReconnect;
	
	private boolean connecting;
	
	@Subscribe
	public void onRender(EventRender e) {
		Minecraft mc = Client.getMinecraft();
		ServerData data = mc.getCurrentServerData();
		if(data != null) {
			lastIp = data.serverIP;
		}
		if(connecting && !(mc.currentScreen instanceof GuiConnecting)) {
			connecting = false;
			lastReconnect = System.currentTimeMillis();
		}
		if(lastIp != null && mc.theWorld == null && !(mc.currentScreen instanceof GuiConnecting)) {
			if(lastReconnect == 0) lastReconnect = System.currentTimeMillis();
			mc.fontRendererObj.drawStringWithShadow(getName(), 0, 0, getActiveColor());
			if(getElapsed() >= 5 && !connecting) {
				mc.displayGuiScreen(new GuiConnecting(new GuiMainMenu(), mc, lastIp, 25565));
				connecting = true;
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
