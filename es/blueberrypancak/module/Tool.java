package es.blueberrypancak.module;

import es.blueberrypancak.Client;
import es.blueberrypancak.event.EventBlockSwing;
import es.blueberrypancak.event.EventCanHarvestBlock;
import es.blueberrypancak.event.EventDigSpeed;
import es.blueberrypancak.event.EventResetBlockRemoving;
import es.blueberrypancak.event.EventStr;
import es.blueberrypancak.event.Subscribe;
import es.blueberrypancak.hook.EntityPlayerSPHook;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketHeldItemChange;

@RegisterModule
public class Tool extends Module {

	private int lastSlot = -1;

	@Subscribe
	public void onPlayerSwing(EventBlockSwing e) {
		IBlockState block = Client.getMinecraft().world.getBlockState(e.getBlockPos());
		swap(getSlot(block));
	}

	@Subscribe
	public void onResetBlockRemoving(EventResetBlockRemoving e) {
		if (lastSlot != Client.getMinecraft().player.inventory.currentItem) {
			swap(Client.getMinecraft().player.inventory.currentItem);
		}
	}

	@Subscribe
	public void onCanHarvestBlock(EventCanHarvestBlock e) {
		if (getTool(e.getBlock()) != null) {
			e.setValue(true);
		}
	}

	@Subscribe
	public void onStrVsBlock(EventStr e) {
		float var2 = 1.0F;
		ItemStack tool = getTool(e.getBlock());
		if (tool != null) {
			var2 *= tool.getStrVsBlock(e.getBlock());
			e.setValue(var2);
		}
	}

	@Subscribe
	public void onDigSpeed(EventDigSpeed e) {
		EntityPlayer p = Client.getMinecraft().player;
		ItemStack tool = getTool(e.getBlock());
		if (tool != null && p.getHeldItemMainhand() != tool) {
			ItemStack last = p.getHeldItemMainhand();
			p.inventory.mainInventory.set(p.inventory.currentItem, tool);
			e.setValue(p.getDigSpeed(e.getBlock()));
			p.inventory.mainInventory.set(p.inventory.currentItem, last);
		}
	}

	private void swap(int slot) {
		if (slot == -1)
			return;
		lastSlot = slot;
		EntityPlayerSPHook player = (EntityPlayerSPHook) Client.getMinecraft().player;
		player.getConnection().sendPacket(new CPacketHeldItemChange(slot));
	}

	private ItemStack getTool(IBlockState block) {
		return Client.getMinecraft().player.inventory.getStackInSlot(getSlot(block));
	}

	private int getSlot(IBlockState block) {
		int slot = -1;
		float f = 1.0F;
		EntityPlayer p = Client.getMinecraft().player;
		for (int i = 0; i < 9; i++) {
			ItemStack o = p.inventory.mainInventory.get(i);
			if (o != null && o.getStrVsBlock(block) > f) {
				slot = i;
				f = o.getStrVsBlock(block);
			}
		}
		return slot;
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
