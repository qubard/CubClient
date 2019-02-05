package client.main.module;

import client.main.Client;
import client.main.event.EventIsSpectator;
import client.main.event.EventRender;
import client.main.event.EventSendPacket;
import client.main.event.Subscribe;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayer;

@RegisterModule(key = 47, color = 10485618, listed = true)
public class Camera extends Module {

	private Position lastPos;

	@Subscribe
	public void onSendPacket(EventSendPacket e) {
		Packet packet = e.getValue();
		if (isEnabled() && (packet instanceof CPacketPlayer || packet instanceof CPacketEntityAction)) {
			e.setCancelled(true);
		}
	}

	@Subscribe
	public void onIsSpectator(EventIsSpectator e) {
		if (isEnabled()) {
			e.setValue(true);
		}
	}

	@Subscribe
	public void onRender(EventRender e) {
		if (isEnabled()) {
			EntityPlayer player = Client.getMinecraft().player;
			player.noClip = true;
			player.setInvisible(true);
			player.capabilities.isFlying = true;
		}
	}

	@Override
	public void onEnabled() {
		EntityPlayer player = Client.getMinecraft().player;
		lastPos = new Position(player.posX, player.posY, player.posZ);
		EntityOtherPlayerMP spawn = new EntityOtherPlayerMP(Client.getMinecraft().world, player.getGameProfile());
		spawn.inventory = player.inventory;
		spawn.inventoryContainer = player.inventoryContainer;
		spawn.rotationYawHead = player.rotationYawHead;
		spawn.setSneaking(player.isSneaking());
		spawn.setPositionAndRotation(player.posX, player.posY, player.posZ, player.rotationYaw, player.rotationPitch);
		Client.getMinecraft().world.addEntityToWorld(-420, spawn);
	}

	@Override
	public void onDisabled() {
		EntityPlayer player = Client.getMinecraft().player;
		player.setPosition(lastPos.getX(), lastPos.getY(), lastPos.getZ());
		player.capabilities.isFlying = false;
		player.noClip = false;
		player.motionX = 0;
		player.motionY = 0;
		player.setInvisible(false);
		player.motionZ = 0;
		Client.getMinecraft().world.removeEntityFromWorld(-420);
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
