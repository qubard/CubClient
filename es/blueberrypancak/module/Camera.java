package es.blueberrypancak.module;

import es.blueberrypancak.Client;
import es.blueberrypancak.event.EventIsSpectator;
import es.blueberrypancak.event.EventRender;
import es.blueberrypancak.event.EventSendPacket;
import es.blueberrypancak.event.Subscribe;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayer;

@RegisterModule(key=47,color=10485618,listed=true)
public class Camera extends Module {
	
	private Position lastPos;
	
	@Subscribe
	public void onSendPacket(EventSendPacket e) {
		Packet packet = e.getValue();
		if(isEnabled() && !(packet instanceof CPacketPlayer.Rotation) && (packet instanceof CPacketPlayer || packet instanceof CPacketEntityAction)) {
			e.setCancelled(true);
		}
	}
	
	@Subscribe
	public void onIsSpectator(EventIsSpectator e) {
		if(isEnabled()) { 
			e.setValue(true);
		}
	}
	
	@Subscribe
	public void onRender(EventRender e) {
		if(isEnabled()) {
			EntityPlayer player = Client.getMinecraft().thePlayer;
			player.noClip = true;
			player.setInvisible(true);
			player.capabilities.isFlying = true;
		}
	}

	@Override
	public void onEnabled() {
		EntityPlayer player = Client.getMinecraft().thePlayer;
		lastPos = new Position(player.posX, player.posY, player.posZ);
	}

	@Override
	public void onDisabled() {
		EntityPlayer player = Client.getMinecraft().thePlayer;
		player.setPosition(lastPos.getX(), lastPos.getY(), lastPos.getZ());
		player.capabilities.isFlying = false;
		player.noClip = false;
		player.motionX = 0;
		player.motionY = 0;
		player.setInvisible(false);
		player.motionZ = 0;
	}

	@Override
	public String getName() {
		return "Camera";
	}
	
	class Position {
		private double x, y, z;
		
		public Position(double x, double y, double z) {
			this.x = x;
			this.y = y;
			this.z = z;
		}
		
		public double getX() {
			return this.x;
		}
		
		public double getY() {
			return this.y;
		}
		
		public double getZ() {
			return this.z;
		}
	}
}
