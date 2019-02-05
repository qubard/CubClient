package client.main.module;

import client.main.Client;
import client.main.event.EventRender;
import client.main.event.EventSetSprint;
import client.main.event.Subscribe;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.EntityPlayer;

@RegisterModule(key = 33, color = 0x00FF21, secondary_color = 0x969696, listed = true)
public class Sprint extends Module {

	@Subscribe
	public void onSetSprint(EventSetSprint e) {
		Minecraft mc = Client.getMinecraft();
		EntityPlayer player = mc.player;
		if (isEnabled() && mc.gameSettings.keyBindForward.isKeyDown()) {
			if (!e.getValue() && (float) player.getFoodStats().getFoodLevel() > 6.0F) {
				e.setValue(true);
			}
		}
	}

	@Subscribe
	public void onRender(EventRender e) {
		Minecraft mc = Client.getMinecraft();
		EntityPlayerSP player = mc.player;
		if (isEnabled() && mc.gameSettings.keyBindForward.isKeyDown()) {
			if (!player.isSprinting()) {
				player.setSprinting((float) player.getFoodStats().getFoodLevel() > 6.0F);
			}
		} else if (!isEnabled()) {
			if (player.isSprinting()) {
				player.setSprinting(false);
			}
		}
		active_color = player.isSprinting() ? getColor() : getSecondaryColor();
	}

	@Override
	public void onEnabled() {

	}

	@Override
	public void onDisabled() {

	}

	@Override
	public String getName() {
		return "Sprint";
	}
}
