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
	
	public static void func_189692_a(FontRenderer p_189692_0_, String p_189692_1_, float p_189692_2_, float p_189692_3_, float p_189692_4_, int p_189692_5_, float p_189692_6_, float p_189692_7_, boolean p_189692_8_, boolean p_189692_9_) {
		System.out.println("CALLED");
	}
}
