package es.blueberrypancak.hook;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;

public class GuiIngameHook extends GuiIngame {

	public GuiIngameHook(Minecraft mcIn) {
		super(mcIn);
	}

	protected void renderPotionEffects(ScaledResolution resolution) {
		return;
	}
	
	public void renderSelectedItem(ScaledResolution scaledRes) {
		return;
	}
	
	protected void renderVignette(float lightLevel, ScaledResolution scaledRes) {
		GlStateManager.depthMask(true);
		GlStateManager.enableDepth();
		return;
	}
	
	protected void renderPumpkinOverlay(ScaledResolution scaledRes) {
		return;
	}
}
