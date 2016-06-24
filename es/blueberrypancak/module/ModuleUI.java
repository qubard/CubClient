package es.blueberrypancak.module;

import es.blueberrypancak.Client;
import es.blueberrypancak.event.EventRender;
import es.blueberrypancak.event.Subscribe;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;

@RegisterModule
public class ModuleUI extends Module {

	@Subscribe
	public void onRender(EventRender e) {
		if(!Client.getMinecraft().inGameHasFocus) return;
		int i = 0;
		Minecraft mc = Client.getMinecraft();
		ScaledResolution res = Client.res();
		FontRenderer f = mc.fontRendererObj;
		for(Module m : Module.getActiveModules()) { 
			Client.getMinecraft().fontRendererObj.drawStringWithShadow(m.getName(), res.getScaledWidth() - f.getStringWidth(m.getName()) - 4, i * 8 + 4, m.getActiveColor());
			i++;
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
		return null;
	}
}
