package es.blueberrypancak;

import java.util.ArrayList;

import com.google.common.reflect.ClassPath;

public class Reflection {

	public static ArrayList<Class<?>> getClasses(final String packageName) {
		final ArrayList<Class<?>> classes = new ArrayList<Class<?>>();
		try {
			final ClassLoader loader = Thread.currentThread().getContextClassLoader();
			for (final ClassPath.ClassInfo info : ClassPath.from(loader).getAllClasses()) {
				if (info.getName().startsWith(packageName)) {
					classes.add(info.load());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return classes;
	}
}
