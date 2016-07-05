package es.blueberrypancak.module;

import es.blueberrypancak.Client;
import es.blueberrypancak.event.EventIsPushed;
import es.blueberrypancak.event.EventRecPacket;
import es.blueberrypancak.event.EventSendPacket;
import es.blueberrypancak.event.EventTick;
import es.blueberrypancak.event.Subscribe;
import es.blueberrypancak.hook.EntityPlayerSPHook;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldProviderHell;

@RegisterModule(key=41,color=11514879,listed=true)
public class NoCheat extends Module {
	
	private boolean falling, wasFalling;
	
	private long lastPickup;
	
	@Subscribe
	public void onSendPacket(EventSendPacket e) {
		Packet packet = e.getValue();
		if(isEnabled() && packet instanceof CPacketPlayer) {
			((CPacketPlayer)packet).onGround = true;
			EntityPlayer p = Client.getMinecraft().thePlayer;
			if(p.fallDistance > 1 && p.motionY < -0.4) {
				e.setValue(new CPacketPlayer.PositionRotation(p.posX, p.posY, p.posZ, p.rotationYaw, 90, p.onGround));
			}
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
	public void onTick(EventTick e) {
		Minecraft mc = Client.getMinecraft();
		EntityPlayerSPHook p = (EntityPlayerSPHook) mc.thePlayer;
		int slot = getWaterBucket();
		if(isEnabled() && slot != -1 && !(mc.theWorld.provider instanceof WorldProviderHell)) {
			if(p.motionY < -0.70) {
				falling = true;
				if(falling && !Client.getMinecraft().theWorld.isAirBlock(new BlockPos(p.posX, p.getEntityBoundingBox().minY-4, p.posZ))) {
					falling = false;
					p.getConnection().sendPacket(new CPacketHeldItemChange(slot));
					p.getConnection().sendPacket(new CPacketPlayerTryUseItem(EnumHand.MAIN_HAND));
					lastPickup = System.currentTimeMillis();
					wasFalling = true;
				}
			}
			if(p.fallDistance > 3) { 
				p.motionX = 0;
				p.motionZ = 0;
			}
		}
		if(p.onGround && getElapsed() >= 300 && wasFalling) {
			p.getConnection().sendPacket(new CPacketPlayer.PositionRotation(p.posX, p.posY, p.posZ, p.rotationYaw, 90, p.onGround));
			p.getConnection().sendPacket(new CPacketPlayerTryUseItem(EnumHand.MAIN_HAND));
			p.getConnection().sendPacket(new CPacketHeldItemChange(p.inventory.currentItem));
			p.getConnection().sendPacket(new CPacketPlayer.PositionRotation(p.posX, p.posY, p.posZ, p.rotationYaw, 0, p.onGround));
			wasFalling = false;
		}
	}
	
	private int getWaterBucket() {
		for(int i = 0; i < 9; i++) {
			ItemStack o = Client.getMinecraft().thePlayer.inventory.mainInventory[i];
			if(o != null && Item.getIdFromItem(o.getItem()) == 326) {
				return i;
			}
		}
		return -1;
	}
	
	private int getElapsed() {
		return (int)(System.currentTimeMillis()-lastPickup);
	}
	
	@Subscribe
	public void onIsPushed(EventIsPushed e) {
		e.setValue(isEnabled() ? false : e.getValue());
	}

	@Override
	public void onEnabled() {
		
	}

	@Override
	public void onDisabled() {
		
	}

	@Override
	public String getName() {
		return "NoCheat";
	}
}
