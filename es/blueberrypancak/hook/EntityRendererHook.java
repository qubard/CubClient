package es.blueberrypancak.hook;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.resources.IResourceManager;

public class EntityRendererHook extends EntityRenderer {

	public EntityRendererHook(Minecraft mcIn, IResourceManager resourceManagerIn) {
		super(mcIn, resourceManagerIn);
	}
	
	public float getFOVModifier(float partialTicks, boolean useFOVSetting) {
		return 90.0F;
	}
}
