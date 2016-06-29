package es.blueberrypancak.module;

import es.blueberrypancak.Client;
import es.blueberrypancak.event.EventRender;
import es.blueberrypancak.event.EventSetSprint;
import es.blueberrypancak.event.Subscribe;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.EntityPlayer;

@RegisterModule(key=33,color=0x00FF21,secondary_color=0x969696,listed=true)
public class Sprint extends Module {

	@Subscribe
	public void onSetSprint(EventSetSprint e) {
		EntityPlayer player = Client.getMinecraft().thePlayer;
		if(isEnabled() && player.moveForward != 0) {
			if(!e.getValue()){ 
				e.setValue(true);
			}
		}
	}
	
	@Subscribe
	public void onRender(EventRender e) {
		EntityPlayerSP player = Client.getMinecraft().thePlayer;
		if(isEnabled() && player.moveForward != 0) {
			if(!player.isSprinting()) {
				player.setSprinting(true);
			}
			/*Vec3d direction = player.getLookVec();
			player.motionX = direction.xCoord;
			player.motionZ = direction.zCoord;
			player.stepHeight = 1.0f;*/
		} else if(!isEnabled() && player.moveForward != 0) {
			if(player.isSprinting()) {
				player.setSprinting(false);
			}
		}
		active_color = player.moveForward != 0 ? getColor() : getSecondaryColor();
	}
	
	@Override
	public void onEnabled() {
		
	}

	@Override
	public void onDisabled() {
		
	}

	@Override
	public String getName() {
		return "Sprint";
	}
}
