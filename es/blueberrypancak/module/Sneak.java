package es.blueberrypancak.module;

import es.blueberrypancak.Client;
import es.blueberrypancak.event.EventIsSneaking;
import es.blueberrypancak.event.EventSendPacket;
import es.blueberrypancak.event.Subscribe;
import es.blueberrypancak.hook.EntityPlayerSPHook;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketEntityAction;

@RegisterModule(key = 19, color = 0xFF0000, secondary_color = 0x00FF00, listed = true)
public class Sneak extends Module {

	@Subscribe
	public void onSendPacket(EventSendPacket e) {
		Packet packet = e.getValue();
		if (isEnabled() && packet instanceof CPacketEntityAction) {
			if (((CPacketEntityAction) packet).getAction() == CPacketEntityAction.Action.STOP_SNEAKING) {
				e.setCancelled(true);
			}
		}
	}

	@Subscribe
	public void onIsSneaking(EventIsSneaking e) {
		if (isEnabled()) {
			EntityPlayerSP p = Client.getMinecraft().player;
			e.setValue(true);
			p.movementInput.sneak = true;
			if (!Module.isEnabled(Camera.class)) {
				p.motionX *= 0.2F;
				p.motionZ *= 0.2F;
			}
		}
	}

	@Override
	public void onEnabled() {
		EntityPlayerSP playerSP = Client.getMinecraft().player;
		EntityPlayerSPHook player = (EntityPlayerSPHook) playerSP;
		player.getConnection().sendPacket(new CPacketEntityAction(playerSP, CPacketEntityAction.Action.START_SNEAKING));
	}

	@Override
	public void onDisabled() {
		EntityPlayerSP playerSP = Client.getMinecraft().player;
		EntityPlayerSPHook player = (EntityPlayerSPHook) playerSP;
		player.getConnection().sendPacket(new CPacketEntityAction(playerSP, CPacketEntityAction.Action.STOP_SNEAKING));
	}

	@Override
	public String getName() {
		return "Sneak";
	}
}
