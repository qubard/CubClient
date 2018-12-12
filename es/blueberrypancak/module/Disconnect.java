package es.blueberrypancak.module;

import es.blueberrypancak.Client;
import es.blueberrypancak.event.EventChat;
import es.blueberrypancak.event.EventRender;
import es.blueberrypancak.event.Subscribe;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.entity.Entity;
import net.minecraft.util.ScreenShotHelper;

@RegisterModule(key = 24, color = 0xFF664F, listed = true)
public class Disconnect extends Module {

	private int threshold = 10;

	@Subscribe
	public void onRender(EventRender e) {
		if (isEnabled()) {
			Minecraft mc = Client.getMinecraft();
			if (getClosestPlayer(mc) != null || mc.player.getFoodStats().getFoodLevel() <= 0
					|| mc.player.getHealth() / 2 <= 5) {
				ScreenShotHelper.saveScreenshot(mc.mcDataDir, mc.displayWidth, mc.displayHeight, mc.getFramebuffer());
				disconnect(mc);
			}
		}
	}

	@Subscribe
	public void onChat(EventChat e) {
		String message = e.getValue();
		if (message.startsWith("-d")) {
			if (message.split(" ").length > 1) {
				this.threshold = Integer.parseInt(message.split(" ")[1]);
			} else {
				this.threshold = -1;
			}
			e.setCancelled(true);
		}
	}

	private EntityOtherPlayerMP getClosestPlayer(Minecraft mc) {
		for (Entity o : mc.world.getLoadedEntityList()) {
			if (o instanceof EntityOtherPlayerMP) {
				EntityOtherPlayerMP p = (EntityOtherPlayerMP) o;
				if (p.getGameProfile() != mc.player.getGameProfile() && !Friend.isFriend(p)) {
					if (p.getDistanceToEntity(mc.player) <= threshold || threshold < 0) {
						return p;
					}
				}
			}
		}
		return null;
	}

	private void disconnect(Minecraft mc) {
		mc.world.sendQuittingDisconnectingPacket();
		mc.loadWorld(null);
		mc.displayGuiScreen(new GuiMainMenu());
		super.remove(this);
	}

	@Override
	public void onEnabled() {

	}

	@Override
	public void onDisabled() {
	}

	@Override
	public String getName() {
		return "Disconnect" + (this.threshold > 0 ? " \247c" + this.threshold : "");
	}
}
