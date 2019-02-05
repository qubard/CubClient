package client.main.module;

import client.main.Client;
import client.main.event.EventRender;
import client.main.event.Subscribe;
import client.main.hook.EntityPlayerSPHook;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.util.EnumHand;

@RegisterModule(key = 49, color = 0x665EFF, listed = true)
public class AntiAFK extends Module {

	private long lastPacket;

	private int nextDelay = 60;

	@Subscribe
	public void onRender(EventRender e) {
		if (lastPacket == 0)
			lastPacket = System.currentTimeMillis();
		if (isEnabled()) {
			if (getElapsed() >= nextDelay) {
				lastPacket = System.currentTimeMillis();
				EntityPlayerSPHook p = (EntityPlayerSPHook) Client.getMinecraft().player;
				p.getConnection().sendPacket(new CPacketAnimation(EnumHand.MAIN_HAND));
			}
		}
	}

	private int getElapsed() {
		return (int) (System.currentTimeMillis() - lastPacket) / 1000;
	}

	@Override
	public void onEnabled() {
		lastPacket = System.currentTimeMillis();
	}

	@Override
	public void onDisabled() {

	}

	@Override
	public String getName() {
		return "AntiAFK \2479" + getElapsed();
	}
}