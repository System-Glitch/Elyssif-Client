package fr.elyssif.client.gui.model;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;

import fr.elyssif.client.ReflectionUtils;
import fr.elyssif.client.StringUtils;
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
	 * Create a new instance of a model with a zero ID.
	 */
	public Model() {
		this(0, null, null);
	}

	/**
	 * Create a new instance of a model.
	 * @param id the ID of the resource
	 */
	public Model(Integer id) {
		this(id, null, null);
	}

	/**
	 * Create a new instance of a model and load it from
	 * the given json object.
	 * @param object the json object from which the attributes
	 * will be loaded.
	 */
	public Model(JsonObject object) {
		loadFromJsonObject(object);
	}

	/**
	 * Create a new instance of a model.
	 * @param id the id of the record
	 * @param createdAt the date of creation
	 * @param updatedAt the date of last update
	 */
	public Model(int id, Date createdAt, Date updatedAt) {
		this.id = new SimpleIntegerProperty(id);
		this.createdAt = new SimpleObjectProperty<Date>(createdAt);
		this.updatedAt = new SimpleObjectProperty<Date>(updatedAt);
	}

	/**
	 * Get the ID of the resource.
	 * @return the id of the record
	 */
	public final SimpleIntegerProperty getId() {
		return id;
	}

	/**
	 * Set the ID of the resource.
	 * @param id the id of the record
	 */
	public final void setId(int id) {
		this.id.set(id);
	}

	/**
	 * Get the date this record was created.
	 * @return the date of creation
	 */
	public final SimpleObjectProperty<Date> getCreatedAt() {
		return createdAt;
	}

	/**
	 * Set the date this record was created.
	 * @param createdAt the date of creation
	 */
	public final void setCreatedAt(Date createdAt) {
		this.createdAt.set(createdAt);
	}

	/**
	 * Get the date this record was last updated.
	 * @return the date of last update
	 */
	public final SimpleObjectProperty<Date> getUpdatedAt() {
		return updatedAt;
	}

	/**
	 * Set the date this record was last updated.
	 * @param updatedAt the date of last update
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
	 * @param the json object from which the attributes
	 * will be loaded.
	 *
	 * @throws RuntimeException if mapped field is not a property or is not found.
	 */
	public final void loadFromJsonObject(JsonObject object) {

		for(Entry<String, JsonElement> element : object.entrySet()) {
			String attributeName = StringUtils.toCamelCase(element.getKey());
			try {
				Field field = ReflectionUtils.findField(getClass(), attributeName, Model.class);
				if(WritableValue.class.isAssignableFrom(field.getType()) && Property.class.isAssignableFrom(field.getType())) {

					fillProperty(field, element.getValue(), attributeName);

				} else if(ObservableList.class.isAssignableFrom(field.getType())) {

					fillList(field, element.getValue().getAsJsonArray(), attributeName);

				} else
					throw new RuntimeException("Field \"" + attributeName + "\" in model \"" + getClass().getSimpleName() + "\" is not a property.");

			} catch(NoSuchFieldException e) {
				Logger.getGlobal().log(Level.SEVERE, "Couldn't find field \"" + attributeName + "\" in model \"" + getClass().getSimpleName() + "\".", e);
			}
		}
	}

	/**
	 * Fill a property with the given json element.
	 * @param field
	 * @param element
	 * @param attributeName
	 */
	private void fillProperty(Field field, JsonElement element, String attributeName) {
		try {
			Method method = ReflectionUtils.findMethod(field.getType(), "set", true);
			field.setAccessible(true);
			method.invoke(field.get(this), getValueFromJson(field, method, element, attributeName));
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException e) {
			Logger.getGlobal().log(Level.SEVERE, "Couldn't set value of field \"" + attributeName + "\" in model \"" + getClass().getSimpleName() + ".", e);
		}
	}

	/**
	 * Fill a list with the given json array
	 * @param field
	 * @param array
	 * @param attributeName
	 */
	private void fillList(Field field, JsonArray array, String attributeName) {
		try {
			Method method = ReflectionUtils.findMethod(field.getType(), "add", true);
			field.setAccessible(true);
			for(JsonElement listElement : array) {
				method.invoke(field.get(this), getValueFromJson(field, method, listElement, attributeName));
			}
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException e) {
			Logger.getGlobal().log(Level.SEVERE, "Couldn't add value to list \"" + attributeName + "\" in model \"" + getClass().getSimpleName() + ".", e);
		}
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
			return element.isJsonNull() ? false : element.getAsBoolean();
		case "int":
		case "Integer":
			return element.isJsonNull() ? 0 : element.getAsInt();
		case "long":
		case "Long":
			return element.isJsonNull() ? 0 : element.getAsLong();
		case "float":
		case "Float":
			return element.isJsonNull() ? Float.NaN : element.getAsFloat();
		case "double":
		case "Double":
			return element.isJsonNull() ? Double.NaN : element.getAsDouble();
		case "String":
			return element.isJsonNull() ? null : element.getAsString();
		case "Object":

			if(element.isJsonNull()) return null;

			if(attributeName.endsWith("At")) { // Attribute is a timestamp
				try {
					return new SimpleDateFormat("yyyy-MM-dd H:m:s").parse(element.getAsString());
				} catch (ParseException e) {
					Logger.getGlobal().log(Level.SEVERE, "Couldn't parse date " + element.getAsString(), e);
					return null;
				}
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
	 * @throws RuntimeException thrown if nested object is not a model nor primitive type
	 * @throws RuntimeException thrown if nested object has multiple type arguments
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

		}

		return null;
	}
}