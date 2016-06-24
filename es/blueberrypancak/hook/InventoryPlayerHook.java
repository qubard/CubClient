package es.blueberrypancak.hook;

import es.blueberrypancak.event.EventManager;
import es.blueberrypancak.event.EventOnStr;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;

public class InventoryPlayerHook extends InventoryPlayer {

	public InventoryPlayerHook(EntityPlayer playerIn) {
		super(playerIn);
	}
	
	public float getStrVsBlock(IBlockState state) {
		EventOnStr e = new EventOnStr(super.getStrVsBlock(state), state);
		EventManager.fire(e);
		return e.getValue();
	}
}
