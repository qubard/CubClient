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
			String s =  "" + (posX >= 0 ? "+" : "") + (int)posX + ", " + (posY >= 0 ? "+" : "") + (int)posY + ", " + (posZ >= 0 ? "+" : "") +(int)posZ + "" + (mobCount != 0 ? " : \247c" + mobCount : "") + "\247f" + (playerCount != 0 ? " : \247b" + playerCount : "");;
			mc.fontRendererObj.drawStringWithShadow(s, 5, 4, 16777215);
		}
	}
	
	private int[] getCount() {
		int i[] = {0,0};
		for(Entity e :  Client.getMinecraft().theWorld.getLoadedEntityList()) {
			if(e instanceof EntityMob) {
				i[0]++;
			} else if(e instanceof EntityOtherPlayerMP) {
				i[1]++;
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
