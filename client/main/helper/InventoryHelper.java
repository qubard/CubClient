package client.main.helper;

import client.main.Client;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class InventoryHelper {

	public static void move(int from, int to) {
		Minecraft mc = Client.getMinecraft();
		EntityPlayer p = mc.player;
		PlayerControllerMP controller = mc.playerController;
		controller.windowClick(0, from, to, ClickType.SWAP, p);
	}
	
	public static int getItem(int id) {
		EntityPlayer p = Client.getMinecraft().player;
		for(int i = 0; i < 36; i++) {
			ItemStack o = p.inventory.mainInventory.get(i);
			if(o != null && Item.getIdFromItem(o.getItem()) == id) {
				return i;
			}
		}
		return -1;
	}

	public static int getEmptySlot() {
		EntityPlayer p = Client.getMinecraft().player;
		for(int i = 0; i < 9; i++) {
			ItemStack o = p.inventory.mainInventory.get(i);
			if(o == null) {
				return i;
			}
		}
		return p.inventory.currentItem;
	}
	
	public static boolean isItem(int slot, int id) {
		try {
			return Item.getIdFromItem(Client.getMinecraft().player.inventory.mainInventory.get(slot).getItem()) == id;
		} catch(Exception e) {
			return false;
		}
	}

	public static boolean isItemOffhand(int id) {
		return Item.getIdFromItem(Client.getMinecraft().player.inventory.offHandInventory.get(0).getItem()) == id;
	}
}