package es.blueberrypancak.module;

import java.util.Random;

import es.blueberrypancak.Client;
import es.blueberrypancak.event.EventOnUpdateEntity;
import es.blueberrypancak.event.EventRender;
import es.blueberrypancak.event.Subscribe;
import es.blueberrypancak.hook.EntityPlayerSPHook;
import net.minecraft.client.Minecraft;
import net.minecraft.network.play.client.CPacketPlayer;

@RegisterModule(key=49,color=0x665EFF,listed=true)
public class AntiAFK extends Module {

	private long lastPacket;
	
	private int start = 10, nextDelay = 60;
	
	@Subscribe
	public void onRender(EventRender e) {
		Minecraft mc = Client.getMinecraft();
		if(lastPacket == 0) lastPacket = System.currentTimeMillis();
		if(isEnabled()) {
			if(getElapsed() >= nextDelay) {
				start = new Random().nextInt(nextDelay-10);
				lastPacket = System.currentTimeMillis();
			}
		}
	}
	
	@Subscribe
	public void onUpdateEntity(EventOnUpdateEntity e) {
		Minecraft mc = Client.getMinecraft();
		if(isEnabled()) {
			if(getElapsed() >= start && getElapsed() <= start+1) {
				mc.thePlayer.moveForward = 1.0F;
			} else if(getElapsed() > start+1 && getElapsed() <= start+3) {
				mc.thePlayer.moveForward = -1.0F;
			}
		}
	}
	
	private int getElapsed() {
		return (int)(System.currentTimeMillis()-lastPacket)/1000;
	}
	
	@Override
	public void onEnabled() {
		lastPacket = System.currentTimeMillis();
	}

	@Override
	public void onDisabled() {
		
	}

	@Override
	public String getName() {
		return "AntiAFK \2479" + (getElapsed());
	}
}
