package es.blueberrypancak.hook;

import es.blueberrypancak.event.EventCanHarvestBlock;
import es.blueberrypancak.event.EventChat;
import es.blueberrypancak.event.EventGetHeldItem;
import es.blueberrypancak.event.EventInWater;
import es.blueberrypancak.event.EventIsOnLadder;
import es.blueberrypancak.event.EventIsPushed;
import es.blueberrypancak.event.EventIsSneaking;
import es.blueberrypancak.event.EventIsSpectator;
import es.blueberrypancak.event.EventManager;
import es.blueberrypancak.event.EventOnLiving;
import es.blueberrypancak.event.EventOnUpdateEntity;
import es.blueberrypancak.event.EventSetSprint;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatisticsManager;
import net.minecraft.world.World;

public class EntityPlayerSPHook extends EntityPlayerSP {

	private NetHandlerPlayClient connection;
	
	public EntityPlayerSPHook(Minecraft mcIn, World worldIn, NetHandlerPlayClient netHandler, StatisticsManager statFile) {
		super(mcIn, worldIn, netHandler, statFile);
		this.connection = netHandler;
	}
	
	public NetHandlerPlayClient getConnection() {
		return this.connection;
	}
	
	public boolean isInWater() { 
		EventInWater e = new EventInWater(super.isInWater());
		EventManager.fire(e);
		return e.getValue();
	}
	
	public void setSprinting(boolean sprinting) {
		EventSetSprint e = new EventSetSprint(sprinting);
		EventManager.fire(e);
		super.setSprinting(e.getValue());
	}
	
	public boolean isSneaking() {
		EventIsSneaking e = new EventIsSneaking(super.isSneaking());
		EventManager.fire(e);
		return e.getValue();
	}
	
	public boolean canHarvestBlock(IBlockState state) {
		EventCanHarvestBlock e = new EventCanHarvestBlock(super.canHarvestBlock(state), state);
		EventManager.fire(e);
		return e.getValue();
	}
	
	public ItemStack getHeldItemMainhand() {
		EventGetHeldItem e = new EventGetHeldItem(super.getHeldItemMainhand());
		EventManager.fire(e);
		return e.getValue();
	}
	
	public boolean isSpectator() {
		EventIsSpectator e = new EventIsSpectator(super.isSpectator());
		EventManager.fire(e);
		return e.getValue();
	}
	
	public void sendChatMessage(String message) {
		EventChat e = new EventChat(message);
		EventManager.fire(e);
		if(!e.isCancelled()) {
			super.sendChatMessage(message);
		}
	}
	
	public void onLivingUpdate() {
		super.onLivingUpdate();
		EventManager.fire(new EventOnLiving());
	}
	
	public boolean isOnLadder() {
		EventIsOnLadder e = new EventIsOnLadder(super.isOnLadder());
		EventManager.fire(e);
		return e.getValue();
	}
	
	public void updateEntityActionState() {
		super.updateEntityActionState();
		EventManager.fire(new EventOnUpdateEntity());
    }
	
	public boolean isPushedByWater() {
		EventIsPushed e = new EventIsPushed(super.isPushedByWater());
		EventManager.fire(e);
		return e.getValue();
	}
}
