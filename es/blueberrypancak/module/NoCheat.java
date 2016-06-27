package es.blueberrypancak.module;

import es.blueberrypancak.Client;
import es.blueberrypancak.event.EventRecPacket;
import es.blueberrypancak.event.EventRender;
import es.blueberrypancak.event.EventSendPacket;
import es.blueberrypancak.event.Subscribe;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.server.SPacketEntityVelocity;

@RegisterModule(key=41,color=11514879,listed=true)
public class NoCheat extends Module {
	
	@Subscribe
	public void onSendPacket(EventSendPacket e) {
		Packet packet = e.getValue();
		if(isEnabled() && packet instanceof CPacketPlayer) {
			((CPacketPlayer)packet).onGround = true;
		}
	}
	
	@Subscribe
	public void onReceivePacket(EventRecPacket e) {
		Packet packet = e.getValue();
		if(isEnabled() && packet instanceof SPacketEntityVelocity) {
			e.setCancelled(true);
		}
	}
	
	@Subscribe
	public void onRender(EventRender e) {
		EntityPlayer p = Client.getMinecraft().thePlayer;
		if(isEnabled() && p.stepHeight != 1.0F) {
			p.stepHeight = 1.0F;
		}
	}

	@Override
	public void onEnabled() {
		Client.getMinecraft().thePlayer.stepHeight = 1.0F;
	}

	@Override
	public void onDisabled() {
		Client.getMinecraft().thePlayer.stepHeight = 0;
	}

	@Override
	public String getName() {
		return "NoCheat";
	}
}
