package es.blueberrypancak.module;

import java.util.List;

import es.blueberrypancak.Client;
import es.blueberrypancak.event.EventCooldown;
import es.blueberrypancak.event.EventRender;
import es.blueberrypancak.event.Subscribe;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;

@RegisterModule(key=34,color=16775680,listed=true)
public class KillAura extends Module {

	private static double distanceThreshold = 36.0D;
	
	private long last;
	
	private int delay = 90;

	@Subscribe
	public void onRender(EventRender e) {
		Minecraft mc = Client.getMinecraft();
		List<Entity> l = mc.theWorld.getLoadedEntityList();
		EntityPlayer p = mc.thePlayer;
		if (isEnabled() && System.currentTimeMillis() > this.last && !p.isHandActive()) {
			Entity o = getClosestEntity();
			if (o != null) {
				hit(p, o);
			}
		}
	}

	@Subscribe
	public void onCooldown(EventCooldown e) {
		if(isEnabled()) {
			e.setValue(0F);
		}
	}

	private Entity getClosestEntity() {
		Minecraft mc = Client.getMinecraft();
		List<Entity> l = mc.theWorld.getLoadedEntityList();
		EntityPlayer p = mc.thePlayer;
		Entity e = null;
		for (Entity o : l) {
			if (o != p && (o instanceof EntityOtherPlayerMP || o instanceof EntityLiving)) {
				if (o.isEntityAlive()) {
					if (e == null || o.getDistanceSqToEntity(p) <= e.getDistanceSqToEntity(p)) {
						if (o.getDistanceSqToEntity(p) <= distanceThreshold) {
							e = o;
						}
					}
				}
			}
		}
		return e;
	}

	private void hit(EntityPlayer p, Entity e) {
		PlayerControllerMP controller = Client.getMinecraft().playerController;
		controller.attackEntity(p, e);
		this.last = System.currentTimeMillis() + this.delay;
	}

	@Override
	public void onEnabled() {

	}

	@Override
	public void onDisabled() {

	}

	@Override
	public String getName() {
		return String.format("%.1f", new Object[] { Double.valueOf(distanceThreshold) }) + "m \u00a76" + delay + "ms";
	}
}
