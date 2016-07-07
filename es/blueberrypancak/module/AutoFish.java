package es.blueberrypancak.module;

import es.blueberrypancak.Client;
import es.blueberrypancak.event.EventRecPacket;
import es.blueberrypancak.event.EventRender;
import es.blueberrypancak.event.Subscribe;
import es.blueberrypancak.hook.EntityPlayerSPHook;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.network.play.server.SPacketParticles;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;

@RegisterModule(key=37,color=0x3F7F47,listed=true)
public class AutoFish extends Module {
	
	@Subscribe
	public void onReceivePacket(EventRecPacket e) {
		Packet packet = e.getValue();
		if(isEnabled() && packet instanceof SPacketParticles) {
			SPacketParticles particle = (SPacketParticles) packet;
			EntityFishHook hook = Client.getMinecraft().thePlayer.fishEntity;
			if(particle.getParticleType() == EnumParticleTypes.WATER_WAKE) {
				if(particle.getParticleCount() == 6 && particle.getParticleSpeed() == 0.2F) {
					EntityPlayerSPHook p = (EntityPlayerSPHook) Client.getMinecraft().thePlayer;
					p.getConnection().sendPacket(new CPacketPlayerTryUseItem(EnumHand.MAIN_HAND));
					p.getConnection().sendPacket(new CPacketPlayerTryUseItem(EnumHand.MAIN_HAND));
				}
			}
		}
	}
	
	@Subscribe
	public void onRender(EventRender e) {
		isEnabled();
	}

	@Override
	public void onEnabled() {
		
	}

	@Override
	public void onDisabled() {
		
	}

	@Override
	public String getName() {
		return "AutoFish";
	}
}
