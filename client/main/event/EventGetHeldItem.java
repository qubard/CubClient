package client.main.event;

import net.minecraft.item.ItemStack;

public class EventGetHeldItem extends EventValue<ItemStack> {

	public EventGetHeldItem(ItemStack value) {
		super(value);
	}
}
