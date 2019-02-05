package client.main.module;

import client.main.Client;
import client.main.event.EventChat;
import client.main.event.EventSendPacket;
import client.main.event.EventUpdateEntity;
import client.main.event.Subscribe;
import net.minecraft.block.material.MaterialLiquid;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;

@RegisterModule(key = 35, color = 0xFF21F3, listed = true)
public class AutoRun extends Module {

	private boolean snapPitch = true, snapYaw = true;

	@Subscribe
	public void onSendPacket(EventSendPacket e) {
		Packet packet = e.getValue();
		if (isEnabled() && packet instanceof CPacketPlayerDigging) {
			CPacketPlayerDigging digging = (CPacketPlayerDigging) packet;
			e.setCancelled(isNearLiquid(digging.getPosition()));
		}
	}

	@Subscribe
	public void onUpdateEntity(EventUpdateEntity e) {
		Minecraft mc = Client.getMinecraft();
		EntityPlayerSP p = mc.player;

		if (isEnabled()) {
			boolean b = true;
			for (double i = -2; i < 3; i += 0.1) {
				b = b & !(mc.world.getBlockState(
						new BlockPos(p.posX + p.getLookVec().xCoord, p.posY - 1 + i, p.posZ + p.getLookVec().zCoord))
						.getMaterial() instanceof MaterialLiquid);
			}
			
			BlockPos pos = new BlockPos(p.posX + p.motionX, p.posY, p.posZ + p.motionZ).down();
			if (b && mc.world.isBlockFullCube(pos) && !mc.world.isAirBlock(pos)) {
				mc.gameSettings.keyBindForward.setKeyBindState(mc.gameSettings.keyBindForward.getKeyCode(), true);
			}
			
			if ((snapYaw || snapPitch) && mc.mouseHelper.deltaX + mc.mouseHelper.deltaY == 0) {
				if (snapYaw) {
					p.rotationYaw = Math.round(p.rotationYaw / 45) * 45;
				}
				if (snapPitch) {
					p.rotationPitch = Math.round(p.rotationPitch / 90) * 90;
				}
			}
		}
	}

	@Subscribe
	public void onChat(EventChat e) {
		String message = e.getValue();
		if (message.equals("-snapPitch")) {
			snapPitch = !snapPitch;
			message((snapPitch ? "\247aEn" : "\247cDis") + "abled snapPitch!");
			e.setCancelled(true);
		} else if (message.equals("-snapYaw")) {
			snapYaw = !snapYaw;
			message((snapYaw ? "\247aEn" : "\247cDis") + "abled snapYaw!");
			e.setCancelled(true);
		}
	}

	private boolean isNearLiquid(BlockPos pos) {
		return isLiquid(pos.east()) || isLiquid(pos.west()) || isLiquid(pos.south()) || isLiquid(pos.north())
				|| isLiquid(pos.up()) || isLiquid(pos.down());
	}

	private boolean isLiquid(BlockPos pos) {
		return Client.getMinecraft().world.getBlockState(pos).getMaterial() instanceof MaterialLiquid;
	}

	private void message(String s) {
		EntityPlayer p = Client.getMinecraft().player;
		p.addChatMessage(new TextComponentString(s));
	}

	@Override
	public void onEnabled() {
		
	}

	@Override
	public void onDisabled() {

	}

	@Override
	public String getName() {
		return "AutoRun";
	}
}
