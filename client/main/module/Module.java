package client.main.module;

import java.util.ArrayList;

import client.main.KeyWrapper;
import client.main.Reflection;
import client.main.event.EventManager;

public abstract class Module {

	private static final ArrayList<Module> activeModules = new ArrayList<Module>();

	private KeyWrapper bind;

	private boolean listed;

	protected int active_color;

	private int color, secondary_color;

	public Module setBind(int key, boolean pressed) {
		this.bind = new KeyWrapper(key, pressed);
		return this;
	}

	public static ArrayList<Module> getActiveModules() {
		return activeModules;
	}
	
	public static boolean isEnabled(Class module) {
		for (Module m : activeModules) {
			if (m.getClass() == module) {
				return true;
			}
		}
		return false;
	}

	public boolean isEnabled() {
		boolean toggle = this.bind.isToggled();
		if (listed) {
			update(toggle);
		}
		return toggle;
	}

	public int getActiveColor() {
		return active_color;
	}

	protected int getColor() {
		return color;
	}

	protected int getSecondaryColor() {
		return secondary_color;
	}

	public Module setListed(boolean listed) {
		this.listed = listed;
		return this;
	}

	public Module setColor(int color, int secondary_color) {
		this.active_color = color;
		this.color = active_color;
		this.secondary_color = secondary_color;
		return this;
	}

	private void update(boolean toggle) {
		if (toggle)
			add(this);
		else
			remove(this);
	}

	private void add(Module m) {
		if (!activeModules.contains(m)) {
			activeModules.add(m);
			this.onEnabled();
		}
	}

	protected void remove(Module m) {
		if (activeModules.contains(m)) {
			activeModules.remove(m);
			this.setBind(this.bind.getKeyCode(), false);
			this.onDisabled();
		}
	}

	public abstract void onEnabled();

	public abstract void onDisabled();

	public abstract String getName();

	public static void registerModules(String packageName) {
		for (Class<?> c : Reflection.getClasses(packageName)) {
			if (c.isAnnotationPresent(RegisterModule.class)) {
				try {
					Object o = c.newInstance();
					if (o instanceof Module) {
						RegisterModule r = c.getAnnotation(RegisterModule.class);
						((Module) o).setBind(r.key(), r.pressed()).setListed(r.listed()).setColor(r.color(),
								r.secondary_color());
						EventManager.register(o);
					}
				} catch (Exception e) {
				}
			}
		}
	}
}