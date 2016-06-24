package es.blueberrypancak.module;

import es.blueberrypancak.Client;
import es.blueberrypancak.event.EventBlockSwing;
import es.blueberrypancak.event.EventOnStr;
import es.blueberrypancak.event.EventRender;
import es.blueberrypancak.event.EventResetBlockRemoving;
import es.blueberrypancak.event.Subscribe;
import es.blueberrypancak.hook.EntityPlayerSPHook;
import es.blueberrypancak.hook.PlayerControllerMPHook;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.network.play.client.CPacketHeldItemChange;

@RegisterModule
public class Tool extends Module {

	@Subscribe
	public void onPlayerSwing(EventBlockSwing e) {
		IBlockState block = Client.getMinecraft().theWorld.getBlockState(e.getBlockPos());
		swap(getSlot(block));
	}

	@Subscribe
	public void onResetBlockRemoving(EventResetBlockRemoving e) {
		swap(Client.getMinecraft().thePlayer.inventory.currentItem);
	}

	@Subscribe
	public void onStrVsBlock(EventOnStr e) {
		float var2 = 1.0F;
		ItemStack tool = getTool(e.getBlock());
		if (tool != null) {
			var2 *= tool.getStrVsBlock(e.getBlock());
			e.setValue(var2);
		}
	}

	private void swap(int slot) {
		if (slot == -1) return;
		EntityPlayerSPHook player = (EntityPlayerSPHook) Client.getMinecraft().thePlayer;
		player.getConnection().sendPacket(new CPacketHeldItemChange(slot));
	}

	private ItemStack getTool(IBlockState block) {
		ItemStack i = Client.getMinecraft().thePlayer.inventory.getStackInSlot(this.getSlot(block));
		return i.getItem() instanceof ItemTool ? i : null;
	}

	private int getSlot(IBlockState block) {
		int slot = 0;
		float f = 0.1F;
		EntityPlayer p = Client.getMinecraft().thePlayer;
		for (int i = 0; i < 9; i++) {
			ItemStack o = p.inventory.mainInventory[i];
			if (o != null && o.getStrVsBlock(block) > f) {
				slot = i;
				f = o.getStrVsBlock(block);
			}
		}
		return p.inventory.getStackInSlot(slot).getItem() instanceof ItemTool ? slot : -1;
	}

	@Override
	public void onEnabled() {

	}

	@Override
	public void onDisabled() {

	}

	@Override
	public String getName() {
		return null;
	}
}
