package es.blueberrypancak.hook;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiNewChat;

public class GuiNewChatHook extends GuiNewChat {

	public GuiNewChatHook(Minecraft mcIn) {
		super(mcIn);
	}
	
	public void drawChat(int updateCounter) {
		super.drawChat(updateCounter);
	}
}
