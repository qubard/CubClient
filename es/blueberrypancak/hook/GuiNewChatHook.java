package es.blueberrypancak.hook;

import es.blueberrypancak.event.EventManager;
import es.blueberrypancak.event.EventOnDrawChat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiNewChat;

public class GuiNewChatHook extends GuiNewChat {

	public GuiNewChatHook(Minecraft mcIn) {
		super(mcIn);
	}
	
	public void drawChat(int updateCounter) {
		EventOnDrawChat e = new EventOnDrawChat(updateCounter);
		EventManager.fire(e);
		if(!e.isCancelled()) {
			super.drawChat(updateCounter);
		}
	}
}
