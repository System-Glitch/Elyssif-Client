package fr.elyssif.client;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

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
			if(method.getName().equals(name) && method.getParameters().length == 1 && (except == null || !method.equals(except))) {
				return method;
			}
		}
		throw new NoSuchMethodException("Method \"" + name + "\" not found in \"" + type + "\".");
	}

	/**
	 * Find field by its name from given class and its super-class.<br><br>
	 *
	 * <code>getFields()</code> only returns <code>public</code> fields.
	 * As a result, we use <code>getDeclaredFields()</code> which returns the private fields.
	 * However it returns only the fields declared in the prompted class, so prompting
	 * the super-class is also needed, hence the double processing in this method.
	 * @param type
	 * @param attributeName
	 * @return field or null if not found
	 * @throws NoSuchFieldException thrown if the field doesn't exist
	 */
	public static Field findField(Class<?> type, String attributeName) throws NoSuchFieldException {
		// TODO Doesn't support more than one inheritance !
		Field field = findField(type.getSuperclass().getDeclaredFields(), attributeName);
		if(field != null) return field;

		field = findField(type.getDeclaredFields(), attributeName);
		if(field == null) throw new NoSuchFieldException("Field \"" + attributeName + "\" not found in \"" + type + "\".");
		return field;
	}

	/**
	 * Find a field by its name in an array of fields.
	 * @param fields
	 * @param attributeName
	 * @return field or null if not found
	 */
	private static Field findField(Field[] fields, String attributeName) {
		for(Field field : fields) {
			if(field.getName().equals(attributeName))
				return field;
		}
		return null;
	}

}
