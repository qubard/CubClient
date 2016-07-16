package es.blueberrypancak.module;

import java.util.ArrayList;

import es.blueberrypancak.Client;
import es.blueberrypancak.event.EventAttack;
import es.blueberrypancak.event.EventChat;
import es.blueberrypancak.event.Subscribe;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextComponentString;

@RegisterModule(listed=false)
public class Friend extends Module {
	
	private static ArrayList<String> friends = new ArrayList<String>();
	
	@Subscribe
	public void onChat(EventChat e) {
		String message = e.getValue();
		if(message.startsWith("-a")) {
			e.setCancelled(true);
			String s = message.split(" ")[1].toLowerCase();
			if(!friends.contains(s)) { 
				friends.add(s);
				message("\247aAdded " + s + "!");
			} else {
				friends.remove(s);
				message("\247cRemoved " + s + "!");
			}
		}
	}
	
	@Subscribe
	public void onAttack(EventAttack e) {
		Entity o = e.getValue();
		if(o instanceof EntityOtherPlayerMP) {
			e.setCancelled(isFriend((EntityOtherPlayerMP) o));
		}
	}
	
	public static boolean isFriend(EntityOtherPlayerMP p) {
		return friends.contains(p.getDisplayName().getUnformattedText().toLowerCase());
	}
	
	private void message(String s) {
		EntityPlayer p = Client.getMinecraft().thePlayer;
		p.addChatMessage(new TextComponentString(s));
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
