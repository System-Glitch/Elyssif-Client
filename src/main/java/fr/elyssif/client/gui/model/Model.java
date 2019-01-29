package fr.elyssif.client.gui.model;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Date;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.WritableValue;
import javafx.collections.ObservableList;

/**
 * Super-class for models. All members of the model may not be filled, depending on the request that created it.
 * 
 * @author Jérémy LAMBERT
 *
 */
public abstract class Model<T> extends RecursiveTreeObject<T> {

	private SimpleIntegerProperty id;
	private SimpleObjectProperty<Date> createdAt;
	private SimpleObjectProperty<Date> updatedAt;

	/**
	 * Create a new instance of a model.
	 * @param id - the ID of the resource
	 */
	public Model(Integer id) {
		this(id, null, null);
	}

	/**
	 * Create a new instance of a model.
	 * @param id
	 * @param createdAt
	 * @param updatedAt
	 */
	public Model(int id, Date createdAt, Date updatedAt) {
		this.id = new SimpleIntegerProperty(id);
		this.createdAt = new SimpleObjectProperty<Date>(createdAt);
		this.updatedAt = new SimpleObjectProperty<Date>(updatedAt);
	}

	/**
	 * Get the ID of the resource.
	 * @return id
	 */
	public final SimpleIntegerProperty getId() {
		return id;
	}

	/**
	 * Set the ID of the resource.
	 * @param id
	 */
	public final void setId(int id) {
		this.id.set(id);
	}

	/**
	 * Get the date this record was created.
	 * @param updatedAt
	 */
	public final SimpleObjectProperty<Date> getCreatedAt() {
		return createdAt;
	}

	/**
	 * Set the date this record was created.
	 * @param updatedAt
	 */
	public final void setCreatedAt(Date createdAt) {
		this.createdAt.set(createdAt);
	}

	/**
	 * Get the date this record was last updated.
	 * @param updatedAt
	 */
	public final SimpleObjectProperty<Date> getUpdatedAt() {
		return updatedAt;
	}

	/**
	 * Set the date this record was last updated.
	 * @param updatedAt
	 */
	public final void setUpdatedAt(Date updatedAt) {
		this.updatedAt.set(updatedAt);
	}

	/**
	 * Get the name of the resource. Will be used in URLs when doing requests.
	 * <br>
	 * For example, a resource having the name "user" will request the REST API to "http://host/api/user" automatically.
	 * @return the name of the resource
	 */
	public String getResourceName() {
		return getClass().getSimpleName().toLowerCase() + 's';
	}

	/**
	 * Load this object's attributes from a json object.
	 * @param object
	 *
	 * @throws RuntimeException if mapped field is not a property or is not found.
	 */
	public final void loadFromJsonObject(JsonObject object) {
		for(Entry<String, JsonElement> element : object.entrySet()) {
			String attributeName = getAttributeName(element.getKey());
			Field field = findField(getClass(), attributeName);
			if(field != null) {
				if(WritableValue.class.isAssignableFrom(field.getType()) && Property.class.isAssignableFrom(field.getType())) {
					Method[] methods = field.getType().getMethods();
					for(Method method : methods) { // Find setter method
						if(method.getName().equals("set")) {

							try {
								Object value = getValueFromJson(field, method, element.getValue(), attributeName);
								if(value != null) {
									field.setAccessible(true);
									method.invoke(field.get(this), value);
								}
							} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
								Logger.getGlobal().log(Level.SEVERE, "Couldn't set value of field \"" + attributeName + "\" in model \"" + getClass().getSimpleName() + ".", e);
							}

							break;
						}
					}
				} else if(ObservableList.class.isAssignableFrom(field.getType())) { // Lists
					Method[] methods = field.getType().getMethods();
					for(Method method : methods) { // Find add method
						if(method.getName().equals("add") && method.getParameters().length == 1) {

							try {
								field.setAccessible(true);
								for(JsonElement listElement : element.getValue().getAsJsonArray()) {
									Object value = getValueFromJson(field, method, listElement, attributeName);
									method.invoke(field.get(this), value);
								}
							} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
								Logger.getGlobal().log(Level.SEVERE, "Couldn't add value to list \"" + attributeName + "\" in model \"" + getClass().getSimpleName() + ".", e);
							}

							break;
						}
					}
				} else
					throw new RuntimeException("Field \"" + attributeName + "\" in model \"" + getClass().getSimpleName() + "\" is not a property.");

			} else
				throw new RuntimeException("Couldn't find field \"" + attributeName + "\" in model \"" + getClass().getSimpleName() + "\".");
		}
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
	 */
	private Field findField(Class<?> type, String attributeName) {
		Field field = findField(type.getSuperclass().getDeclaredFields(), attributeName);
		if(field != null) return field;

		field = findField(type.getDeclaredFields(), attributeName);
		return field;
	}

	/**
	 * Find a field by its name in an array of fields.
	 * @param fields
	 * @param attributeName
	 * @return field or null if not found
	 */
	private Field findField(Field[] fields, String attributeName) {
		for(Field field : fields) {
			if(field.getName().equals(attributeName))
				return field;
		}
		return null;
	}

	/**
	 * Get the value of the given element with the correct type for the given method.
	 * @param field
	 * @param method
	 * @param element
	 * @param attributeName
	 * @return value got from the given element
	 */
	private Object getValueFromJson(Field field, Method method, JsonElement element, String attributeName) {
		return getValueFromJson(field, method, element, attributeName, null);
	}

	/**
	 * Get the value of the given element with the correct type for the given method.
	 * @param field
	 * @param method
	 * @param element
	 * @param attributeName
	 * @param paramType nullable
	 * @return value got from the given element
	 */
	private Object getValueFromJson(Field field, Method method, JsonElement element, String attributeName, Class<?> paramType) {

		Class<?> type = paramType != null ? paramType : method.getParameterTypes()[0];
		switch(type.getSimpleName()) {
		case "boolean":
		case "Boolean":
			return element.getAsBoolean();
		case "short":
		case "Short":
			return element.getAsShort();
		case "int":
		case "Integer":
			return element.getAsInt();
		case "long":
		case "Long":
			return element.getAsLong();
		case "float":
		case "Float":
			return element.getAsFloat();
		case "double":
		case "Double":
			return element.getAsDouble();
		case "String":
			return element.getAsString();
		case "Object":

			if(attributeName.endsWith("At")) { // Attribute is a timestamp
				return new Date(element.getAsLong() * 1000); // *1000 because value given in secods
			}

			Object value = getObjectFromJson(field, method, element, attributeName);
			if(value != null) return value;
		}
		Logger.getGlobal().warning("Unsupported type: " + type.getSimpleName() + " for attribute \"" + attributeName + "\" in model \"" + getClass().getSimpleName() + "\".");
		return null;
	}

	/**
	 * Handle "Object" and "String" parameter type for auto-loading properties from json.
	 * @param field
	 * @param method
	 * @param element
	 * @param attributeName
	 * @return value or null if not supported
	 */
	private Object getObjectFromJson(Field field, Method method, JsonElement element, String attributeName) {
		Type fieldType = field.getGenericType();
		if(fieldType instanceof ParameterizedType) {
			ParameterizedType pType = (ParameterizedType) fieldType;
			if(pType.getActualTypeArguments().length == 1) {
				Class<?> genericType = (Class<?>) pType.getActualTypeArguments()[0];
				if(Model.class.isAssignableFrom(genericType)) {
					try {
						Model<?> model = (Model<?>) genericType.getConstructor(Integer.class).newInstance(0);
						model.loadFromJsonObject(element.getAsJsonObject());
						return model;
					} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
							| InvocationTargetException | NoSuchMethodException | SecurityException e) {
						Logger.getGlobal().log(Level.SEVERE, "Couldn't instantiate nested model \"" + attributeName + "\" in model \"" + getClass().getSimpleName() + ".", e);
					}
				} else if(!genericType.getSimpleName().equals("Object")) {
					return getValueFromJson(field, method, element, attributeName, genericType);
				} else
					throw new RuntimeException("Nested object \"" + attributeName + "\" in model \"" + getClass().getSimpleName() + "\" is not a Model nor primitive type.");

			} else
				throw new RuntimeException("Couldn't load nested object \"" + attributeName + "\" in model \"" + getClass().getSimpleName() + "\": multiple type arguments.");

		} else { // Special case for strings (since reflection returns Object for StringProperty)
			try {
				method = field.getType().getMethod(method.getName(), String.class);
				return element.getAsString();
			} catch(NoSuchMethodException e) {
				// Do nothing if method not found
			}
		}

		return null;
	}

	/**
	 * Convert the snake case attribute name to camel case.
	 * @param entryName
	 * @return attributeName
	 */
	private String getAttributeName(String entryName) {
		StringBuilder builder = new StringBuilder(entryName);
		int index = builder.indexOf("_");
		while(index != -1 || index >= entryName.length() - 1) {
			builder.setCharAt(index + 1, Character.toUpperCase(builder.charAt(index + 1)));
			builder.deleteCharAt(index);
			index = builder.indexOf("_");
		}
		return builder.toString();
	}
}