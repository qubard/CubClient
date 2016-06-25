package es.blueberrypancak;

import es.blueberrypancak.hook.EntityRendererHook;
import es.blueberrypancak.hook.GuiIngameHook;
import es.blueberrypancak.hook.RenderGlobalHook;
import es.blueberrypancak.module.Module;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;

public class Client {
	
	private static Minecraft mc;
	
	public Client(Minecraft mc) {
		this.mc = mc;
		registerHooks();
		Module.registerModules("es.blueberrypancak.module");
	}
	
	private void registerHooks() {
		mc.ingameGUI = new GuiIngameHook(mc);
		mc.entityRenderer = new EntityRendererHook(mc, mc.getResourceManager());
		mc.renderGlobal = new RenderGlobalHook(mc);
	}
	
	public static Minecraft getMinecraft() {
		return mc;
	}
	
	public static ScaledResolution res() {
		return new ScaledResolution(mc);
	}
}
