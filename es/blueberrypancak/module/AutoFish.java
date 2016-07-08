package es.blueberrypancak.module;

import es.blueberrypancak.Client;
import es.blueberrypancak.event.EventRecPacket;
import es.blueberrypancak.event.EventRender;
import es.blueberrypancak.event.EventSendPacket;
import es.blueberrypancak.event.Subscribe;
import es.blueberrypancak.hook.EntityPlayerSPHook;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.entity.Entity;
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

@RegisterModule(key=37,color=0x3F7F47,secondary_color=0xFF1C07,listed=true)
public class AutoFish extends Module {
	
	private int lastSlot = -1;
	
	@Subscribe
	public void onSendPacket(EventSendPacket e) {
		Packet packet = e.getValue();
		if(isEnabled() && packet instanceof CPacketHeldItemChange) {
			CPacketHeldItemChange change = (CPacketHeldItemChange) packet;
			if(lastSlot != -1 && change.getSlotId() != lastSlot) {
				e.setValue(new CPacketHeldItemChange(lastSlot));
			}
		}
	}
	
	@Subscribe
	public void onReceivePacket(EventRecPacket e) {
		Packet packet = e.getValue();
		if(isEnabled() && packet instanceof SPacketParticles) {
			SPacketParticles particle = (SPacketParticles) packet;
			if(particle.getParticleType() == EnumParticleTypes.WATER_WAKE) {
				if(particle.getParticleCount() == 6 && particle.getParticleSpeed() == 0.2F) {
					if(equipRod()){
						toss();
						toss();
					}
				}
			}
		}
	}
	
	private boolean equipRod() {
		int slot = getFishingRod();
		if(slot < 0) return false;
		EntityPlayerSPHook p = (EntityPlayerSPHook) Client.getMinecraft().thePlayer;
		int chosenSlot = getEmptySlot();
		if(slot >= 9) {
			move(slot, chosenSlot);
		}
		lastSlot = slot >= 9 ? chosenSlot : slot;
		p.getConnection().sendPacket(new CPacketHeldItemChange(lastSlot));
		return true;
	}
	
	private int getEmptySlot() {
		EntityPlayer p = Client.getMinecraft().thePlayer;
		for(int i = 0; i < 9; i++) {
			ItemStack o = p.inventory.mainInventory[i];
			if(o == null) {
				return i;
			}
		}
		return p.inventory.currentItem;
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
	
	private boolean isFishing() {
		Minecraft mc = Client.getMinecraft();
		for(Entity e : mc.theWorld.loadedEntityList) {
			if(e instanceof EntityFishHook) {
				if(e.getDistanceToEntity(mc.thePlayer) < 15) {
					return true;
				}
			}
		}
		return mc.thePlayer.fishEntity != null;
	}
	
	@Subscribe
	public void onRender(EventRender e) {
		if(isEnabled()) {
			if(lastSlot == -1 || Client.getMinecraft().thePlayer.inventory.mainInventory[lastSlot] == null) {
				onEnabled();
			}
			active_color = getFishingRod() != -1 ? getColor() : getSecondaryColor();
		}
	}

	@Override
	public void onEnabled() {
		if(equipRod()) {
			toss();
		}
	}

	@Override
	public void onDisabled() {
		if(isFishing()) {
			onEnabled();
		}
		EntityPlayerSPHook p = (EntityPlayerSPHook) Client.getMinecraft().thePlayer;
		p.getConnection().sendPacket(new CPacketHeldItemChange(p.inventory.currentItem));
	}

	@Override
	public String getName() {
		return getFishingRod() != -1 ? "AutoFish" : "No rod!";
	}
}
