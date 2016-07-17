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
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;

@RegisterModule(key=37,color=0x3F7F47,secondary_color=0xFF1C07,listed=true)
public class AutoFish extends Module {
	
	private long nextTick;
	
	private int lastSlot = -1;
	
	@Subscribe
	public void onSendPacket(EventSendPacket e) {
		Packet packet = e.getValue();
		if(isEnabled() && packet instanceof CPacketHeldItemChange && getFishingRod() != -1) {
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
					EntityFishHook o = getFishHook();
					if(o != null && o.getDistance(particle.getXCoordinate(), particle.getYCoordinate(), particle.getZCoordinate()) < 1.5) {
						if(equipRod()){
							toss();
							toss();
						}
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
	
	private EntityFishHook getFishHook() {
		Minecraft mc = Client.getMinecraft();
		for(Entity e : mc.theWorld.loadedEntityList) {
			if(e instanceof EntityFishHook) {
				EntityFishHook o = (EntityFishHook) e;
				if(o.angler == mc.thePlayer) {
					return o;
				}
			}
		}
		return mc.thePlayer.fishEntity;
	}
	
	private boolean isFishing() {
		return getFishHook() != null;
	}
	
	private boolean facingWater(int dist) {
		Minecraft mc = Client.getMinecraft();
		EntityPlayer p = mc.thePlayer;
		Vec3d vec3d = ((Entity)p).getPositionEyes(1F);
		Vec3d vec3d1 = ((Entity)p).getLook(1F);
		Vec3d vec3d2 = vec3d.addVector(vec3d1.xCoord * dist, vec3d1.yCoord * dist, vec3d1.zCoord * dist);
		RayTraceResult result = mc.theWorld.rayTraceBlocks(vec3d, vec3d2, true, false, true);
		if(result.typeOfHit == RayTraceResult.Type.BLOCK) {
			return mc.theWorld.getBlockState(result.getBlockPos()).toString().contains("water");
		}
		return false;
	}
	
	@Subscribe
	public void onRender(EventRender e) {
		if(isEnabled()) {
			if(lastSlot == -1 || Client.getMinecraft().thePlayer.inventory.mainInventory[lastSlot] == null || !isFishing()) {
				if(System.currentTimeMillis() > nextTick) { 
					onEnabled();
				}
			}
			active_color = getFishingRod() != -1 ? getColor() : getSecondaryColor();
		}
	}

	@Override
	public void onEnabled() {
		if(equipRod() && facingWater(15)) {
			toss();
		}
		nextTick = System.currentTimeMillis() + 1500;
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
