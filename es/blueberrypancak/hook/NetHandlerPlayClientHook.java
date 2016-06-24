package es.blueberrypancak.hook;

import com.mojang.authlib.GameProfile;

import es.blueberrypancak.event.EventManager;
import es.blueberrypancak.event.EventSendPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;

public class NetHandlerPlayClientHook extends NetHandlerPlayClient {

	public NetHandlerPlayClientHook(Minecraft mcIn, GuiScreen p_i46300_2_, NetworkManager networkManagerIn, GameProfile profileIn) {
		super(mcIn, p_i46300_2_, networkManagerIn, profileIn);
	}
	
	public void sendPacket(Packet<?> packetIn) {
		EventSendPacket e = new EventSendPacket(packetIn);
		EventManager.fire(e);
		if(!e.isCancelled()) {
			super.sendPacket(e.getValue());
		}
	}    

}
