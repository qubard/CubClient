package es.blueberrypancak.module;

import es.blueberrypancak.Client;
import es.blueberrypancak.event.EventOnUpdateEntity;
import es.blueberrypancak.event.Subscribe;
import net.minecraft.block.material.MaterialLiquid;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.math.BlockPos;

@RegisterModule(key=35,color=0xFF21F3,listed=true)
public class AutoRun extends Module {

	@Subscribe
	public void onUpdateEntity(EventOnUpdateEntity e) {
		Minecraft mc = Client.getMinecraft();
		EntityPlayerSP p = mc.thePlayer;
		if(isEnabled()) {
			boolean b = true;
			for(double i = -2; i < 3; i += 0.1) { 
				b =  b&!(mc.theWorld.getBlockState(new BlockPos(p.posX+p.getLookVec().xCoord, p.posY-1+i, p.posZ+p.getLookVec().zCoord)).getMaterial() instanceof MaterialLiquid);
			}
			BlockPos pos = new BlockPos(p.posX+p.motionX, p.posY-1, p.posZ+p.motionZ);
			if(b && mc.theWorld.isBlockFullCube(pos) && !mc.theWorld.isAirBlock(pos)) {
				p.moveForward = 1.0F;
			}
			if(mc.mouseHelper.deltaX + mc.mouseHelper.deltaY == 0) {
				p.rotationYaw = Math.round(p.rotationYaw/45)*45;
				p.rotationPitch = Math.round(p.rotationPitch/90)*90;
			}
		}
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
