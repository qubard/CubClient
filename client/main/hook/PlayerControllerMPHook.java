package client.main.hook;

import client.main.Client;
import client.main.event.EventAttack;
import client.main.event.EventBlockBreak;
import client.main.event.EventBlockSwing;
import client.main.event.EventManager;
import client.main.event.EventPlayerDamageBlock;
import client.main.event.EventResetBlockRemoving;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.stats.RecipeBook;
import net.minecraft.stats.StatisticsManager;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class PlayerControllerMPHook extends PlayerControllerMP {

	private NetHandlerPlayClient connection;
	
	public PlayerControllerMPHook(Minecraft mcIn, NetHandlerPlayClient netHandler) {
		super(mcIn, netHandler);
		this.connection = netHandler;
	}
	
	public boolean onPlayerDestroyBlock(BlockPos pos) {
		EventManager.fire(new EventBlockBreak(pos));
		return super.onPlayerDestroyBlock(pos);
	}
	
	public boolean clickBlock(BlockPos loc, EnumFacing face) {
		EventManager.fire(new EventBlockSwing(loc,face));
		return super.clickBlock(loc, face);
	}
	
	public EntityPlayerSP func_192830_a(World worldIn, StatisticsManager statWriter, RecipeBook recipe) {
		return new EntityPlayerSPHook(Client.getMinecraft(), worldIn, this.connection, statWriter, recipe);
	}
	
	public boolean onPlayerDamageBlock(BlockPos posBlock, EnumFacing directionFacing) {
		EventManager.fire(new EventPlayerDamageBlock(posBlock, directionFacing));
		return super.onPlayerDamageBlock(posBlock, directionFacing);
	}
	
	public void resetBlockRemoving() {
		EventManager.fire(new EventResetBlockRemoving());
		super.resetBlockRemoving();
	}
	
	public void attackEntity(EntityPlayer playerIn, Entity targetEntity) {
		EventAttack e = new EventAttack(targetEntity);
		EventManager.fire(e);
		if(!e.isCancelled()) {
			super.attackEntity(playerIn, targetEntity);
		}
	}
}
