package es.blueberrypancak.module;

import es.blueberrypancak.Client;
import es.blueberrypancak.event.EventRecPacket;
import es.blueberrypancak.event.EventRender;
import es.blueberrypancak.event.Subscribe;
import es.blueberrypancak.hook.EntityPlayerSPHook;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.ItemFishingRod;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketHeldItemChange;
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
					if(equipRod()){
						toss();
						toss();
					}
				}
			}
		}
	}
	
	private boolean equipRod() {
		EntityPlayerSPHook p = (EntityPlayerSPHook) Client.getMinecraft().thePlayer;
		int slot = getFishingRod();
		if(slot < 0) return false;
		if(slot > 9) {
			move(slot, p.inventory.currentItem);
		}
		p.getConnection().sendPacket(new CPacketHeldItemChange(slot));
		return true;
	}
	
	private int getFishingRod() {
		EntityPlayer p = Client.getMinecraft().thePlayer;
		for(int i = 0; i < 36; i++) {
			ItemStack o = p.inventory.mainInventory[i];
			if(o != null && o.getItem() instanceof ItemFishingRod) {
				return i;
			}
		}
		return -1;
	}
	
	private void move(int from, int to) {
		Minecraft mc = Client.getMinecraft();
		EntityPlayer p = mc.thePlayer;
		PlayerControllerMP controller = mc.playerController;
		controller.windowClick(0, from, to, ClickType.SWAP, p);
	}
	
	private void toss() {
		EntityPlayerSPHook p = (EntityPlayerSPHook) Client.getMinecraft().thePlayer;
		p.getConnection().sendPacket(new CPacketPlayerTryUseItem(EnumHand.MAIN_HAND));
	}
	
	@Subscribe
	public void onRender(EventRender e) {
		isEnabled();
	}

	@Override
	public void onEnabled() {
		if(equipRod()) {
			toss();
		}
	}

	@Override
	public void onDisabled() {
		onEnabled();
	}

	@Override
	public String getName() {
		return "AutoFish";
	}
}
