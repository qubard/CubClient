package es.blueberrypancak.module;

import es.blueberrypancak.Client;
import es.blueberrypancak.event.EventChat;
import es.blueberrypancak.event.EventSendPacket;
import es.blueberrypancak.event.EventUpdateEntity;
import es.blueberrypancak.event.Subscribe;
import net.minecraft.block.material.MaterialLiquid;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;

@RegisterModule(key=35,color=0xFF21F3,listed=true)
public class AutoRun extends Module {
	
	private boolean snapPitch = true, snapYaw = true;
	
	@Subscribe
	public void onSendPacket(EventSendPacket e) {
		Packet packet = e.getValue();
		if(isEnabled() && packet instanceof CPacketPlayerDigging) {
			CPacketPlayerDigging digging = (CPacketPlayerDigging) packet;
			e.setCancelled(isNearLiquid(digging.getPosition()));
		}
	}

	@Subscribe
	public void onUpdateEntity(EventUpdateEntity e) {
		Minecraft mc = Client.getMinecraft();
		EntityPlayerSP p = mc.thePlayer;
		if(isEnabled()) {
			boolean b = true;
			for(double i = -2; i < 3; i += 0.1) { 
				b =  b&!(mc.theWorld.getBlockState(new BlockPos(p.posX+p.getLookVec().xCoord, p.posY-1+i, p.posZ+p.getLookVec().zCoord)).getMaterial() instanceof MaterialLiquid);
			}
			BlockPos pos = new BlockPos(p.posX+p.motionX, p.posY, p.posZ+p.motionZ).down();
			if(b && mc.theWorld.isBlockFullCube(pos) && !mc.theWorld.isAirBlock(pos)) {
				p.moveForward = 1.0F;
			}
			if((snapYaw || snapPitch) && mc.mouseHelper.deltaX + mc.mouseHelper.deltaY == 0) {
				if(snapYaw) { 
					p.rotationYaw = Math.round(p.rotationYaw/45)*45;
				}
				if(snapPitch) { 
					p.rotationPitch = Math.round(p.rotationPitch/90)*90;
				}
			}
		}
	}
	
	@Subscribe
	public void onChat(EventChat e) {
		String message = e.getValue();
		if(message.equals("-snapPitch")) {
			snapPitch = !snapPitch;
			message((snapPitch ? "\247aEn" : "\247cDis") + "abled snapPitch!");
			e.setCancelled(true);
		} else if(message.equals("-snapYaw")) {
			snapYaw = !snapYaw;
			message((snapYaw ? "\247aEn" : "\247cDis") + "abled snapYaw!");
			e.setCancelled(true);
		}
	}
	
	private boolean isNearLiquid(BlockPos pos) {
		Minecraft mc = Client.getMinecraft();
		for(int x = -2; x < 2; x++) {
			for(int y = -2; y < 2; y++) { 
				for(int z = -2; z < 2; z++) { 
					if(mc.theWorld.getBlockState(new BlockPos(pos.getX()+x, pos.getY()+y, pos.getZ()+z)).getMaterial() instanceof MaterialLiquid) {
						return true;
					}
				}
			}
		}
		return false;
	}
	
	private void message(String s) {
		EntityPlayer p = Client.getMinecraft().thePlayer;
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
