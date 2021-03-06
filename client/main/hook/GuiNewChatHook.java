package client.main.hook;

import client.main.event.EventDrawChat;
import client.main.event.EventManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiNewChat;

public class GuiNewChatHook extends GuiNewChat {

	public GuiNewChatHook(Minecraft mcIn) {
		super(mcIn);
	}
	
	public void drawChat(int updateCounter) {
		EventDrawChat e = new EventDrawChat(updateCounter);
		EventManager.fire(e);
		if(!e.isCancelled()) {
			super.drawChat(updateCounter);
		}
	}
}
