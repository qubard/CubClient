package client.main.hook;

import client.main.event.EventCanHarvestBlock;
import client.main.event.EventChat;
import client.main.event.EventDigSpeed;
import client.main.event.EventGetHeldItem;
import client.main.event.EventInLava;
import client.main.event.EventInWater;
import client.main.event.EventIsOnLadder;
import client.main.event.EventIsPushed;
import client.main.event.EventIsSneaking;
import client.main.event.EventIsSpectator;
import client.main.event.EventManager;
import client.main.event.EventOnLiving;
import client.main.event.EventRespawn;
import client.main.event.EventSetSprint;
import client.main.event.EventUpdateEntity;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.RecipeBook;
import net.minecraft.stats.StatisticsManager;
import net.minecraft.world.World;

public class EntityPlayerSPHook extends EntityPlayerSP {

	private NetHandlerPlayClient connection;
	
	public EntityPlayerSPHook(Minecraft mcIn, World worldIn, NetHandlerPlayClient netHandler, StatisticsManager statFile, RecipeBook recipe) {
		super(mcIn, worldIn, netHandler, statFile, recipe);
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
	
	public boolean isInLava() {
		EventInLava e = new EventInLava(super.isInLava());
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
	
	 public float getDigSpeed(IBlockState state) {
		 EventDigSpeed e = new EventDigSpeed(state, super.getDigSpeed(state));
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
		EventManager.fire(new EventUpdateEntity());
    }
	
	public boolean isPushedByWater() {
		EventIsPushed e = new EventIsPushed(super.isPushedByWater());
		EventManager.fire(e);
		return e.getValue();
	}
	
	public void respawnPlayer() {
		EventRespawn e = new EventRespawn();
		EventManager.fire(e);
		super.respawnPlayer();
	}
}
