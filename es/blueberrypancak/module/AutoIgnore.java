package es.blueberrypancak.module;

import java.util.Stack;

import es.blueberrypancak.event.EventRecPacket;
import es.blueberrypancak.event.Subscribe;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketChatMessage;
import net.minecraft.network.play.server.SPacketChat;

@RegisterModule
public class AutoIgnore extends Module {
	
	private Stack<Message> spam = new Stack<Message>();
	
	@Subscribe
	public void onReceivePacket(EventRecPacket e) {
		Packet packet = e.getValue();
		if(packet instanceof SPacketChat) {
			String text[] = ((SPacketChat)packet).getChatComponent().getUnformattedText().split(" ");
			Message message = new Message(text[0], text[1]);
			Message top = spam.size() > 0 ? spam.peek() : null;
			if(top != null && top.equals(message) || spam.size() == 0)  {
				spam.push(message);
			} else if(spam.size() > 0 && !top.equals(message)) {
				spam.clear();
			}
			
			if(spam.size() >= 3) {
				for(Message m : spam) {
					// ignore each sender
				}
			}
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

	class Message {
		private String sender, message;
		
		public Message(String sender, String message) {
			this.sender = sender;
			this.message = message;
		}
		
		public String getSender() { 
			return this.sender;
		}
		
		public String getMessage() { 
			return this.message;
		}
		
		public boolean equals(Message m) {
			return message.equals(m.getMessage()) && !sender.equals(m.getSender());
		}
	}
}
