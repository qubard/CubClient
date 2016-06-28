package es.blueberrypancak.module;

import java.util.Random;

import es.blueberrypancak.Client;
import es.blueberrypancak.event.EventRender;
import es.blueberrypancak.event.Subscribe;
import es.blueberrypancak.hook.EntityPlayerSPHook;
import net.minecraft.client.Minecraft;
import net.minecraft.network.play.client.CPacketPlayer;

@RegisterModule(key=49,color=0x665EFF,listed=true)
public class AntiAFK extends Module {

	private long lastPacket;
	
	private int nextDelay;
	
	@Subscribe
	public void onRender(EventRender e) {
		Minecraft mc = Client.getMinecraft();
		if(lastPacket == 0) lastPacket = System.currentTimeMillis();
		if(nextDelay == 0) nextDelay = (int)new Random().nextInt(3);
		if(isEnabled()) {
			if(getElapsed() > nextDelay) {
				EntityPlayerSPHook p = (EntityPlayerSPHook) mc.thePlayer;
				float rYaw = new Random().nextFloat()*90*(new Random().nextInt(1) == 0 ? 1 : -1);
				float rPitch = new Random().nextFloat()*90*(new Random().nextInt(1) == 0 ? 1 : -1);
				p.getConnection().sendPacket(new CPacketPlayer.Rotation(rYaw, rPitch, true));
				nextDelay = (int)new Random().nextInt(10);
				lastPacket = System.currentTimeMillis();
			}
		}
	}
	
	private int getElapsed() {
		return (int)(System.currentTimeMillis()-lastPacket)/1000;
	}
	
	@Override
	public void onEnabled() {
		
	}

	@Override
	public void onDisabled() {
		
	}

	@Override
	public String getName() {
		return "AntiAFK \2479" + (nextDelay-getElapsed());
	}

}
