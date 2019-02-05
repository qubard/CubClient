package client.main;

import org.apache.logging.log4j.LogManager;

import client.main.hook.EntityRendererHook;
import client.main.hook.GuiIngameHook;
import client.main.hook.RenderGlobalHook;
import client.main.module.Module;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;

public class Client {
	
	private static Minecraft mc;
	
	public Client(Minecraft mc) {
		this.mc = mc;
		registerHooks();
		Module.registerModules("client.main.module");
		LogManager.getLogger().info(Client.getMinecraft().getSession().getSessionID());
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
