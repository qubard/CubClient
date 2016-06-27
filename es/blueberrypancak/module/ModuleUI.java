package es.blueberrypancak.module;

import es.blueberrypancak.Client;
import es.blueberrypancak.event.EventRender;
import es.blueberrypancak.event.Subscribe;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;

@RegisterModule
public class ModuleUI extends Module {

	@Subscribe
	public void onRender(EventRender e) {
		Minecraft mc = Client.getMinecraft();
		if(!mc.inGameHasFocus) return;
		int i = 0;
		ScaledResolution res = Client.res();
		FontRenderer f = mc.fontRendererObj;
		for(Module m : Module.getActiveModules()) { 
			mc.fontRendererObj.drawStringWithShadow(m.getName(), res.getScaledWidth() - f.getStringWidth(m.getName()) - 4, i * 8 + 4, m.getActiveColor());
			i++;
		}
		GlStateManager.disableLighting();
	}
	
	@Override
	public void onEnabled() {
	}

	@Override
	public void onDisabled() {
		
	}

	@Override
	public String getName() {
		return null;
	}
}
