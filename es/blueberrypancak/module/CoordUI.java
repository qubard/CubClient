package es.blueberrypancak.module;

import es.blueberrypancak.Client;
import es.blueberrypancak.event.EventRender;
import es.blueberrypancak.event.Subscribe;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityMob;

@RegisterModule(key=25)
public class CoordUI extends Module {

	@Subscribe
	public void onRender(EventRender e) { 
		Minecraft mc = Client.getMinecraft();
		if(isEnabled() && mc.inGameHasFocus){ 
			int[] count = getCount();
			int mobCount = count[0];
			int playerCount = count[1];
			int posX = (int) mc.thePlayer.posX;
			int posY = (int) mc.thePlayer.posY;
			int posZ = (int) mc.thePlayer.posZ;
			String s =  (posX >= 0 ? "+" : "") + (int)posX + ", " + (posY >= 0 ? "+" : "") + (int)posY + ", " + (posZ >= 0 ? "+" : "") +(int)posZ + (mobCount != 0 ? "\247f \247c" + mobCount + "" : "") + (playerCount != 0 ? " \247b" + playerCount : "") + "\247a " + mc.getDebugFPS() + "fps";
			mc.fontRendererObj.drawStringWithShadow(s, 5, 4, 16777215);
		}
	}
	
	private int[] getCount() {
		int i[] = {0,0};
		Minecraft mc = Client.getMinecraft();
		for(Entity e :  mc.theWorld.getLoadedEntityList()) {
			if(e instanceof EntityMob && e.getDistanceSqToEntity(mc.thePlayer) < 30) {
				i[0]++;
			} else if(e instanceof EntityOtherPlayerMP ) {
				EntityOtherPlayerMP p = (EntityOtherPlayerMP) e;
				if(p.getGameProfile() != mc.thePlayer.getGameProfile() && !Friend.isFriend(p)) {
					i[1]++;
				}
			}
		}
		return i;
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
