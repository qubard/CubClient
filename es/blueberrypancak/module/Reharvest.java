package es.blueberrypancak.module;

import es.blueberrypancak.Client;
import es.blueberrypancak.event.EventChat;
import es.blueberrypancak.event.EventSendPacket;
import es.blueberrypancak.event.EventTick;
import es.blueberrypancak.event.Subscribe;
import es.blueberrypancak.helper.InventoryHelper;
import es.blueberrypancak.hook.EntityPlayerSPHook;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.Item;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;

@RegisterModule(key=200,color=0xFF1C36,listed=true)
public class Reharvest extends Module {
	
	private long nextTick;
	
	private int lastSlot = -1;
	private int delay = 150;
	
	private final int BONEMEAL = 351;
	private final int WHEAT_SEEDS = 295;
	private final int BONEBLOCK = 216;
	
	private BlockPos soil;
	
	@Subscribe
	public void onSendPacket(EventSendPacket e) {
		Packet packet = e.getValue();
		if(isEnabled() && packet instanceof CPacketHeldItemChange && getBonemeal() != -1) {
			CPacketHeldItemChange change = (CPacketHeldItemChange) packet;
			if(lastSlot != -1 && change.getSlotId() != lastSlot) {
				e.setValue(new CPacketHeldItemChange(lastSlot));
			}
		}
	}

	@Subscribe
	public void onTick(EventTick e) {
		if(isEnabled() && (InventoryHelper.isItem(lastSlot, BONEMEAL) || equipBonemeal())) {
			Minecraft mc = Client.getMinecraft();
			EntityPlayerSPHook p = (EntityPlayerSPHook) Client.getMinecraft().thePlayer;
			if (System.currentTimeMillis() >= nextTick && (p.noClip || p.getDistanceSq(soil) < 6)) {
				String seeds = mc.theWorld.getBlockState(soil.up()).toString();
				String block = mc.theWorld.getBlockState(soil).toString();
				if(seeds.contains("wheat")) {
					if(!seeds.contains("age=7")) {
						p.getConnection().sendPacket(new CPacketPlayerTryUseItemOnBlock(soil.up(), EnumFacing.UP, EnumHand.MAIN_HAND, 0,0,0));
						p.getConnection().sendPacket(new CPacketAnimation(EnumHand.MAIN_HAND));
					} else {
						p.getConnection().sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, soil.up(), EnumFacing.DOWN));
						p.getConnection().sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, soil.up(), EnumFacing.DOWN));
						p.getConnection().sendPacket(new CPacketAnimation(EnumHand.MAIN_HAND));
					}
				} else if(block.contains("moisture")) {
					p.getConnection().sendPacket(new CPacketPlayerTryUseItemOnBlock(soil, EnumFacing.UP, EnumHand.OFF_HAND, soil.getX(), soil.getY(), soil.getZ()));
					p.getConnection().sendPacket(new CPacketAnimation(EnumHand.OFF_HAND));
				}
				nextTick = System.currentTimeMillis() + delay;
			}
		}
	}
	
	@Subscribe
	public void onChat(EventChat e) {
		String message = e.getValue();
		if(message.startsWith("-r")) {
			if(message.split(" ").length > 1) {
				this.delay = Integer.parseInt(message.split(" ")[1]);
			} else {
				this.delay = 150;
			}
			e.setCancelled(true);
		}
	}
	
	public boolean isEnabled() {
		return super.isEnabled() && Item.getIdFromItem(Client.getMinecraft().thePlayer.getHeldItemOffhand().getItem()) == WHEAT_SEEDS && findSoil();
	}
	
	private boolean equipBonemeal() {
		int slot = getBonemeal();
		if(slot < 0) { 
			boneBlock();
			slot = getBonemeal();
			if(slot < 0) { 
				return false;
			}
		}
		EntityPlayerSPHook p = (EntityPlayerSPHook) Client.getMinecraft().thePlayer;
		lastSlot = slot;
		if(slot >= 9) {
			int chosenSlot = InventoryHelper.getEmptySlot();
		 	InventoryHelper.move(slot, chosenSlot);
		 	lastSlot = chosenSlot;
		}
		p.getConnection().sendPacket(new CPacketHeldItemChange(lastSlot));
		return true;
	}
	
	private void boneBlock() {
		int slot = InventoryHelper.getItem(BONEBLOCK);
		if(slot != -1) {
			EntityPlayer p = Client.getMinecraft().thePlayer;
			PlayerControllerMP controller = Client.getMinecraft().playerController;
			controller.windowClick(0, slot < 9 ? slot+36 : slot, 0, ClickType.PICKUP, p);
			controller.windowClick(0, 1, 0, ClickType.PICKUP, p);
			controller.windowClick(0, 0, 0, ClickType.QUICK_MOVE, p);
		}
	}
	
	private int getBonemeal() {
		return InventoryHelper.getItem(BONEMEAL);
	}
	
	private boolean findSoil() {
		Minecraft mc = Client.getMinecraft();
		if(soil == null && mc.objectMouseOver != null && mc.objectMouseOver.typeOfHit == RayTraceResult.Type.BLOCK) {
			soil = mc.objectMouseOver.getBlockPos();
			String block = mc.theWorld.getBlockState(soil).toString();
			if(!block.contains("moisture")) {
				soil = soil.down();
				block = mc.theWorld.getBlockState(soil).toString();
				if(!block.contains("moisture")) {
					soil = null;
				}
			}
		}
		return soil != null;
	}
	
	@Override
	public void onEnabled() {
		findSoil();
		equipBonemeal();
	}

	@Override
	public void onDisabled() {
		soil = null;
		EntityPlayerSPHook p = (EntityPlayerSPHook) Client.getMinecraft().thePlayer;
		p.getConnection().sendPacket(new CPacketHeldItemChange(p.inventory.currentItem));
	}

	@Override
	public String getName() {
		return soil != null ? delay != 150 ? delay + "ms" : "Reharvest" : "Can't find soil";
	}
}
