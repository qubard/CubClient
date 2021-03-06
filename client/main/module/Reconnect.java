package client.main.module;

import client.main.Client;
import client.main.event.EventDisconnect;
import client.main.event.EventRender;
import client.main.event.Subscribe;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.multiplayer.GuiConnecting;
import net.minecraft.client.multiplayer.ServerData;

@RegisterModule(color = 0xFF9028)
public class Reconnect extends Module {

	private String lastIp;

	private long lastReconnect;

	@Subscribe
	public void onRender(EventRender e) {
		Minecraft mc = Client.getMinecraft();
		ServerData data = mc.getCurrentServerData();
		if (data != null) {
			lastIp = data.serverIP;
		}
		if (lastIp != null && mc.world == null && !(mc.currentScreen instanceof GuiConnecting)) {
			if (lastReconnect == 0)
				lastReconnect = System.currentTimeMillis();
			mc.fontRendererObj.drawStringWithShadow(getName(), 0, 0, getActiveColor());
			if (getElapsed() >= 5) {
				mc.displayGuiScreen(new GuiConnecting(new GuiMainMenu(), mc, lastIp, 25565));
				lastReconnect = System.currentTimeMillis();
			}
		}
	}

	@Subscribe
	public void onDisconnect(EventDisconnect e) {
		lastReconnect = System.currentTimeMillis();
	}

	private int getElapsed() {
		return (int) (System.currentTimeMillis() - lastReconnect) / 1000;
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
