package client.main.hook;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.entity.Entity;

public class RenderGlobalHook extends RenderGlobal {

	public RenderGlobalHook(Minecraft mcIn) {
		super(mcIn);
	}
	
	public void renderEntities(Entity renderViewEntity, ICamera camera, float partialTicks) {
		super.renderEntities(renderViewEntity, camera, partialTicks);
	}
}
