package es.blueberrypancak;

import es.blueberrypancak.module.Module;
import net.minecraft.client.settings.KeyBinding;

public class KeyWrapper {
	
	private KeyBinding bind;
	
	private Module module;
	
	private boolean pressed, active;
	
	public KeyWrapper(int keyCode, boolean pressed) {
		this.pressed = pressed;
		this.bind = new KeyBinding("key."+keyCode, keyCode, "key.categories.misc");
	}
	
	public KeyWrapper(int keyCode, boolean pressed, Module module) {
		this(keyCode, pressed);
		this.module = module;
	}

	public boolean isToggled() {
		if(bind.getKeyCode() == -1) return true;
		
		if(pressed) { 
			return this.bind.isKeyDown();
		}
		
		if(this.bind.isPressed()) {
			active = !active;
			if(module != null) { 
				if(active) module.onEnabled();
				else module.onDisabled();
			}
		}
		return active;
	}
}
