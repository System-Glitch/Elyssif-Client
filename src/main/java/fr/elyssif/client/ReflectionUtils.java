package fr.elyssif.client;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.logging.Logger;

/**
 * Reflection-related utilities.
 * 
 * @author Jérémy LAMBERT
 *
 */
public abstract class ReflectionUtils {

	/**
	 * Find method by its name. Only returns method that have one parameter
	 * @param type
	 * @param name
	 * @return method
	 * @throws NoSuchMethodException
	 */
	public static Method findMethod(Class<?> type, String name) throws NoSuchMethodException {
		Method method = findMethod(type, name, null);

		if(method != null && method.getParameterTypes()[0].equals(Object.class)) { // In case method is overridden
			try {
				return findMethod(type, name, method);
			} catch(NoSuchMethodException e) {
				// Do nothing if not found
			}
		}

		return method;
	}

	/**
	 * Find method by its name, excluding the given except method. Only returns method that have one parameter
	 * @param type the class in which the method will be searched
	 * @param name the name of the method
	 * @param except a method to exclude from the search
	 * @return method
	 * @throws NoSuchMethodException thrown if the method doesn't exist
	 */
	private static Method findMethod(Class<?> type, String name, Method except) throws NoSuchMethodException {
		Method[] methods = type.getMethods();
		for(Method method : methods) {
			if(method.getName().equals(name) && !method.isSynthetic() && method.getParameters().length == 1 && (except == null || !method.equals(except))) {
				return method;
			}
		}
		throw new NoSuchMethodException("Method \"" + name + "\" not found in \"" + type + "\".");
	}

	/**
	 * <p>Find field by its name from given class and its ancestors.</p>
	 *
	 * @param type the class in which to search for the wanted attribute
	 * @param attributeName the name of the wanted attribute
	 * @return field or null if not found
	 * @throws NoSuchFieldException thrown if the field doesn't exist
	 * @throws IllegalArgumentException thrown if <code>lastAncestor</code> is not a parent of <code>type</code>.
	 */
	public static Field findField(Class<?> type, String attributeName) throws NoSuchFieldException {
		return findField(type, attributeName, Object.class);
	}

	/**
	 * <p>Find field by its name from given class and its ancestors until
	 * the given <code>lastAncestor</code>.</p>
	 *
	 * @param type the class in which to search for the wanted attribute
	 * @param attributeName the name of the wanted attribute
	 * @param lastAncestor the last ancestor to get the fields from
	 * @return field or null if not found
	 * @throws NoSuchFieldException thrown if the field doesn't exist
	 * @throws IllegalArgumentException thrown if <code>lastAncestor</code> is not a parent of <code>type</code>.
	 */
	public static Field findField(Class<?> type, String attributeName, Class<?> lastAncestor) throws NoSuchFieldException {
		HashMap<String, Field> fields = getFields(type, lastAncestor);

		if(!fields.containsKey(attributeName)) {
			throw new NoSuchFieldException("Field \"" + attributeName + "\" not found in \"" + type + "\".");
		}

		return fields.get(attributeName);
	}

	/**
	 * <p>Get a list of all fields declared in the given <code>type</code> and
	 * its inherited fields until the given <code>lastAncestor</code>.</p>
	 * <p>The attributes are retrieved from bottom (<code>type</code>) to top
	 * (<code>lastAncestor</code>). If two attributes with the same name are found,
	 * only the one the least high in the inheritance is kept.</p>
	 * @param type the class from which the fields will be retrieved
	 * @param lastAncestor the last ancestor to get the fields from
	 * @return a map of the fields. The key being the field name.
	 * @throws IllegalArgumentException thrown if <code>lastAncestor</code> is not a parent of <code>type</code>.
	 */
	public static HashMap<String, Field> getFields(Class<?> type, Class<?> lastAncestor) {
		if(!lastAncestor.isAssignableFrom(type)) throw new IllegalArgumentException(lastAncestor.getName() + " is not a parent of " + type.getName());

		var fields = new HashMap<String, Field>();
		var clazz = type;

		while(clazz != null) {

			for(Field field : clazz.getDeclaredFields())
				if(!fields.containsKey(field.getName()) && !field.isSynthetic()) {
					fields.put(field.getName(), field);
					Logger.getGlobal().info(field.getName());
				}

			if(clazz.equals(lastAncestor)) break;

			clazz = clazz.getSuperclass();
		}

		return fields;
	}

}
