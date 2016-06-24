package es.blueberrypancak.event;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class EventManager {
	
	private static final HashMap<Class<? extends Event>, List<MethodData>> REGISTRY = new HashMap<Class<? extends Event>, List<MethodData>>();

	/**
	 * Add the target listener to the registry based on the event it's subscribed to.
	 * @param target
	 */
	public static void register(Object target) {
		Method[] methods = target.getClass().getDeclaredMethods();
		for(final Method method : methods) {
			if(validAnnotation(method)) {
				Class<? extends Event> key = (Class<? extends Event>) method.getParameterTypes()[0];
				CopyOnWriteArrayList<MethodData> l = new CopyOnWriteArrayList<MethodData>();
				REGISTRY.putIfAbsent(key, l);
				if(!REGISTRY.get(key).contains(method)) {
					REGISTRY.get(key).add(new MethodData(method, target));
				}
			}
		}
	}
	
	/**
	 * Fire an event to all of our listeners associated with the event.
	 * @param event
	 * @return
	 */
	public static Event fire(Event event) {
		List<MethodData> methods = REGISTRY.get(event.getClass());
		if(methods != null) {
			for(MethodData m : methods) {
				try {
					m.getMethod().invoke(m.getTarget(), event);
				} catch(Exception e) {
					continue;	
				}
			}
		}
		return event;
	}
	
	private static boolean validAnnotation(Method method) {
		return method.isAnnotationPresent(Subscribe.class) && method.getParameterCount() == 1;
	}
	
	public static final class MethodData {
		
		private final Method method;
		private final Object target;
		
		public MethodData(Method m, Object target) {
			this.method = m;
			this.target = target;
		}
		
		public Method getMethod() {
			return this.method;
		}
		
		public Object getTarget() {
			return this.target;
		}
	}
}
