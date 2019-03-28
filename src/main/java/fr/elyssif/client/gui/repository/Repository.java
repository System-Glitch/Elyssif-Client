package fr.elyssif.client.gui.repository;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.Date;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.http.client.HttpClient;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import fr.elyssif.client.Config;
import fr.elyssif.client.ReflectionUtils;
import fr.elyssif.client.StringUtils;
import fr.elyssif.client.gui.controller.LogoutCallback;
import fr.elyssif.client.gui.controller.MainController;
import fr.elyssif.client.gui.model.Model;
import fr.elyssif.client.gui.model.ModelCallback;
import fr.elyssif.client.gui.view.Paginator;
import fr.elyssif.client.http.Authenticator;
import fr.elyssif.client.http.FailCallback;
import fr.elyssif.client.http.FormCallback;
import fr.elyssif.client.http.HttpMethod;
import fr.elyssif.client.http.JsonCallback;
import fr.elyssif.client.http.PaginateCallback;
import fr.elyssif.client.http.RestCallback;
import fr.elyssif.client.http.RestRequest;
import fr.elyssif.client.http.RestResponse;
import javafx.beans.property.Property;
import javafx.beans.value.WritableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * The <b>Repository Pattern</b> is an addition to the MVC pattern.
 * It fits right between the Controller and the Model so the controller never interacts directly with the Model.
 *
 * The aim is to:
 * <ul>
 * <li>Lighten controllers by moving the query building and logic into the repositories.</li>
 * <li>Improve readability and maintainability.</li>
 * <li>Reduce code redundancy as the super-class <code>Repository</code> contains most frequent queries.</li>
 * </ul>
 *
 * @author Jérémy LAMBERT
 * @see Model
 */
public abstract class Repository<T extends Model<T>> {

	private static final String API_URL = "/api/";

	private HttpClient httpClient;
	private Authenticator authenticator;

	private boolean authenticated; // Defines if sent requests use authentication

	private T model; // Empty model for reference

	/**
	 * Create a new Repository instance using
	 * the global http client and authenticator.
	 */
	public Repository() {
		this(MainController.getInstance().getHttpClient(), MainController.getInstance().getAuthenticator());
	}

	/**
	 * Create a new Repository instance using the global authenticator.
	 * @param client the http client used for queries
	 */
	public Repository(HttpClient client) {
		this(client, MainController.getInstance().getAuthenticator());
	}

	/**
	 * Create a new Repository instance using the global authenticator.
	 * @param authenticator the authenticator used for authenticated queries
	 */
	public Repository(Authenticator authenticator) {
		this(MainController.getInstance().getHttpClient(), authenticator);
	}

	/**
	 * Create a new Repository instance.
	 * @param httpClient the http client used for queries
	 * @param authenticator the authenticator used for authenticated queries
	 */
	public Repository(HttpClient httpClient, Authenticator authenticator) {
		this.httpClient = httpClient;
		this.authenticator = authenticator;
		this.authenticated = true;

		model = instantiateReferenceModel();
	}

	/**
	 * Create an instance of the reference model from generic type.
	 */
	private final T instantiateReferenceModel() {
		try {
			ParameterizedType superClass = (ParameterizedType) getClass().getGenericSuperclass();

			@SuppressWarnings("unchecked")
			Class<T> type = (Class<T>) superClass.getActualTypeArguments()[0];
			return type.getConstructor(Integer.class).newInstance(0);
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			Logger.getGlobal().log(Level.SEVERE, "Couldn't instantiate reference Model.", e);
		}
		return null;
	}

	/**
	 * Create a new instance of the reference model for each
	 * element in the given JsonArray.
	 * @param array the items array
	 * @return list
	 */
	private final ObservableList<T> parseArray(JsonArray array) {
		ObservableList<T> list = FXCollections.observableArrayList();
		for(JsonElement element : array) {
			if(element.isJsonObject()) {
				T model = instantiateReferenceModel();
				model.loadFromJsonObject(element.getAsJsonObject());
				list.add(model);
			} else
				Logger.getGlobal().warning("Array element is not an object. Skip.\n\t" + element.toString());
		}

		return list;
	}

	/**
	 * Handle a paginate response. Prepare and execute callbacks.
	 * @param response the response
	 * @param callback the callback to execute on success
	 * @param failCallback the callback to execute if the response is invalid
	 */
	private final void handlePaginateResponse(RestResponse response, PaginateCallback<? extends Model<?>> callback, FailCallback failCallback) {
		JsonElement element = response.getJsonElement();
		if(element.isJsonObject()) {

			JsonObject object = element.getAsJsonObject();
			if(object.has("data")) {
				JsonElement itemsElement = object.get("data");
				if(itemsElement.isJsonArray()) {
					ObservableList<? extends Model<?>> list = parseArray(itemsElement.getAsJsonArray());
					Paginator<? extends Model<?>> paginator = new Paginator<>(list);
					paginator.loadFromJson(object);

					callback.setResponse(response);
					callback.setPaginator(paginator);
					callback.run();

				} else handleMalformedResponse(response, failCallback, "\"items\" attribute to be an array");
			} else handleMalformedResponse(response, failCallback, "missing attribute \"data\"");
		} else handleMalformedResponse(response, failCallback, "JSON object");
	}

	/**
	 * Set if the repository needs to add an access token to every request.<br>
	 * The token used will be the one from the Authenticator given at the instantiation.
	 * @param authenticated
	 */
	protected final void setAuthenticated(boolean authenticated) {
		this.authenticated = authenticated;
	}

	/**
	 * Get if the repository needs to add an access token to every request.
	 * @return authenticated
	 */
	public final boolean isAuthenticated() {
		return authenticated;
	}

	/**
	 * <p>Get all attributes values from the given <code>model</code></p>
	 * <p>Ignores null attributes, lists and nested models.</p>
	 * <p>Override this method if you want to manually provide the attributes,
	 * to include a list or nested object if needed for example.</p>
	 * @param model the model to extract the attributes from
	 * @return a map of the attributes, the key being the attribute name in snake case
	 */
	protected HashMap<String, Object> getAttributes(T model) {
		var attributes = new HashMap<String, Object>();
		HashMap<String, Field> fields = ReflectionUtils.getFields(model.getClass(), Model.class);

		for(Field field : fields.values()) {
			if(WritableValue.class.isAssignableFrom(field.getType()) && Property.class.isAssignableFrom(field.getType())) {

				try {
					Method method = ReflectionUtils.findMethod(field.getType(), "get", false);

					if(Model.class.isAssignableFrom(method.getReturnType())) // Ignore nested models
						continue;

					field.setAccessible(true);
					Object value = method.invoke(field.get(model));

					if(value != null)
						attributes.put(StringUtils.toSnakeCase(field.getName()), value);
				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException e) {
					Logger.getGlobal().log(Level.SEVERE, "Couldn't get value of field \"" + field.getName() + "\" in model \"" + model.getClass().getSimpleName() + "\".", e);
				}

			}
		}

		return attributes;
	}

	/**
	 * Execute a request without parameters.
	 *
	 * @param action the action (last segment of the url)
	 * @param method the method to use in the request
	 *
	 * @see HttpMethod
	 */
	protected final void request(String action, HttpMethod method) {
		request(action, method, null, null, null);
	}

	/**
	 * Execute a request without parameters and return the raw result. If the given <code>callback</code>
	 * is a <code>JsonCallback</code>, the parsed JSON will be passed to it.
	 *
	 * @param action the action (last segment of the url)
	 * @param method the method to use in the request
	 * @param callback the callback to execute when the request is done, nullable.
	 *
	 * @see HttpMethod
	 * @see RestCallback
	 */
	protected final void request(String action, HttpMethod method, RestCallback callback) {
		request(action, method, null, callback, null);
	}

	/**
	 * Execute a request without parameters and return the raw result. If the given <code>callback</code>
	 * is a <code>JsonCallback</code>, the parsed JSON will be passed to it.
	 *
	 * @param action the action (last segment of the url)
	 * @param method the method to use in the request
	 * @param callback the callback to execute when the request is done, nullable.
	 * @param failCallback the callback to execute if the request fails, nullable.
	 *
	 * @see HttpMethod
	 * @see RestCallback
	 * @see FailCallback
	 */
	protected final void request(String action, HttpMethod method, RestCallback callback, FailCallback failCallback) {
		request(action, method, null, callback, failCallback);
	}

	/**
	 * Execute a request. If the given <code>callback</code>
	 * is a <code>JsonCallback</code>, the parsed JSON will be passed to it.
	 *
	 * @param action the action (last segment of the url)
	 * @param method the method to use in the request
	 * @param params a map of parameters. If <code>method</code> is <code>GET</code>, the parameters
	 * will be URL parameters and body parameters otherwise. Nullable.
	 *
	 * @see HttpMethod
	 */
	protected final void request(String action, HttpMethod method, HashMap<String, ?> params) {
		request(action, method, params, null, null);
	}

	/**
	 * Execute a request and return the raw result. If the given <code>callback</code>
	 * is a <code>JsonCallback</code>, the parsed JSON will be passed to it.
	 *
	 * @param action the action (last segment of the url)
	 * @param method the method to use in the request
	 * @param params a map of parameters. If <code>method</code> is <code>GET</code>, the parameters
	 * will be URL parameters and body parameters otherwise. Nullable.
	 * @param callback the callback to execute when the request is done, nullable.
	 *
	 * @see HttpMethod
	 * @see RestCallback
	 */
	protected final void request(String action, HttpMethod method, HashMap<String, ?> params, RestCallback callback) {
		request(action, method, params, callback, null);
	}

	/**
	 * Execute a request and return the raw result. If the given <code>callback</code>
	 * is a <code>JsonCallback</code>, the parsed JSON will be passed to it.
	 *
	 * @param action the action (last segment of the url)
	 * @param method the method to use in the request
	 * @param params a map of parameters. If <code>method</code> is <code>GET</code>, the parameters
	 * will be URL parameters and body parameters otherwise. Nullable.
	 * @param callback the callback to execute when the request is done, nullable.
	 * @param failCallback the callback to execute if the request fails, nullable.
	 *
	 * @see HttpMethod
	 * @see RestCallback
	 * @see FailCallback
	 */
	protected final void request(String action, HttpMethod method, HashMap<String, ?> params, RestCallback callback, FailCallback failCallback) {
		RestRequest request = new RestRequest(httpClient, Config.getInstance().get("Host") + API_URL + model.getResourceName() + (action != "" ? "/" + action : ""));

		if(params != null) { // Set params
			if(method.equals(HttpMethod.GET)) { // Set URL params if method is GET
				for(Entry<String, ?> entry : params.entrySet()) {
					request.urlParam(entry.getKey(), entry.getValue());
				}
			} else { // Set body params if method is not GET
				for(Entry<String, ?> entry : params.entrySet()) {
					request.param(entry.getKey(), entry.getValue());
				}
			}
		}

		if(authenticated && authenticator != null) {
			request.setAuthorizationToken(authenticator.getToken());
		}

		request.asyncExecute(method, new RestCallback() {

			@Override
			public void run() {
				RestResponse response = getResponse();
				if(response.isSuccessful()) {
					if(callback != null) {

						callback.setResponse(response);
						if(callback instanceof JsonCallback)
							((JsonCallback) callback).setElement(response.getJsonElement());

						callback.run();
					}
				} else {
					if(failCallback != null) {
						failCallback.setResponse(response);
						failCallback.run();
					}
				}

				if(response.getStatus() == 401 && authenticator != null) { // Unauthenticated
					authenticator.logout(new LogoutCallback());
				} else if(!response.isSuccessful() && response.getStatus() != 422) {
					Logger.getGlobal().warning("Repository request failed: " + method.name() + " " + response.getStatus() + " " + failCallback.getMessage());
				}
			}

		});
	}

	/**
	 * Prepare and execute a fail callback and log a warning.<br>
	 * This method should only be called when a request was successful but
	 * its response was malformed (missing attribute in response for example).
	 *
	 * @param response the response to pass to the fail callback
	 * @param failCallback the fail callback to execute, nullable
	 * @param expected the name of the expected element
	 */
	protected final void handleMalformedResponse(RestResponse response, FailCallback failCallback, String expected) {
		if(failCallback != null) {
			failCallback.setResponse(response);
			failCallback.setMessage("%malformed-response");
			failCallback.run();
		}
		Logger.getGlobal().warning("Malformed response (expected " + expected + "):\n\t" + response.getRawBody());
	}

	/**
	 * Get a record by its id.
	 * @param id the id of the requested record, msut be positive
	 * @param callback the callback executed on success
	 */
	public void getById(int id, ModelCallback<T> callback) {
		getById(id, callback, null);
	}

	/**
	 * Get a record by its id.
	 * @param id the id of the requested record, msut be positive
	 * @param callback the callback executed on success
	 * @param failCallback the callback executed on failure, nullable
	 * @throws IllegalArgumentException thrown if <code>id</code> isn't positive
	 */
	public void getById(int id, ModelCallback<T> callback, FailCallback failCallback) {
		if(id <= 0) throw new IllegalArgumentException("ID must be positive, " + id + " given.");
		request(String.valueOf(id), HttpMethod.GET, new JsonCallback() {

			public void run() {
				T model = instantiateReferenceModel();
				if(model != null) {
					if(getElement().isJsonObject()) {
						model.loadFromJsonObject(getElement().getAsJsonObject());
						callback.setResponse(getResponse());
						callback.setModel(model);
						callback.run();
					} else {
						handleMalformedResponse(getResponse(), failCallback, "JSON object");
					}
				}
			}

		}, failCallback);
	}

	/**
	 * Get a paginate of all the records.
	 * @param page the page number, must be positive
	 * @param callback the callback executed on success
	 * @throws IllegalArgumentException thrown if <code>page</code> isn't positive
	 */
	public void getAll(int page, PaginateCallback<T> callback) {
		getAll(page, callback, null);
	}

	/**
	 * Get a paginate of all the records.
	 * @param page the page number, must be positive
	 * @param callback the callback executed on success
	 * @param failCallback the callback executed on failure, nullable
	 * @throws IllegalArgumentException thrown if <code>page</code> isn't positive
	 */
	public void getAll(int page, PaginateCallback<T> callback, FailCallback failCallback) {
		if(page <= 0) throw new IllegalArgumentException("Page number must be positive, " + page + " given.");

		var params = new HashMap<String, Integer>();
		params.put("page", page);
		get(params, callback, failCallback);
	}

	/**
	 * Get a paginate of all the records matching the given <code>search</code>.
	 * @param search the search keyword(s)
	 * @param callback the callback executed on success
	 */
	public void getWhere(String search, PaginateCallback<? extends Model<?>> callback) {
		getWhere(search, callback, null);
	}

	/**
	 * Get a paginate of all the records matching the given <code>search</code>.
	 * @param search the search keyword(s)
	 * @param callback the callback executed on success
	 * @param failCallback the callback executed on failure, nullable
	 */
	public void getWhere(String search, PaginateCallback<? extends Model<?>> callback, FailCallback failCallback) {
		var params = new HashMap<String, String>();
		params.put("search", search);
		get(params, callback, failCallback);
	}

	/**
	 * Execute a generic GET request
	 * @param params the URL params for the request
	 * @param callback the callback executed on success
	 * @param failCallback the callback executed on failure, nullable
	 */
	private void get(HashMap<String, ?> params, PaginateCallback<? extends Model<?>> callback, FailCallback failCallback) {
		request("", HttpMethod.GET, params, new JsonCallback() { // Empty action for index

			public void run() {
				handlePaginateResponse(getResponse(), callback, failCallback);
			}

		}, failCallback);
	}

	/**
	 * <p>Create and store a model on the server.</p>
	 * <p>The record will be created with all the non-null
	 * fields inside the given <code>model</code>. Lists will be ignored too.
	 * On success, the given <code>model</code> is updated with
	 * the values of the new resource. You can then use it safely.
	 * The instance passed to the given <code>callback</code> equals
	 * the given <code>model</code>.</p>
	 * @param model the model to store on the server
	 * @param callback the callback executed on success
	 * @param formCallback the callback executed on validation error
	 */
	public void store(T model, ModelCallback<T> callback, FormCallback formCallback) {
		store(model, callback, formCallback, null);
	}

	/**
	 * <p>Create and store a model on the server.</p>
	 * <p>The record will be created with all the non-null
	 * fields inside the given <code>model</code>. Lists will be ignored too.
	 * On success, the given <code>model</code> is updated with
	 * the values of the new resource. You can then use it safely.
	 * The instance passed to the given <code>callback</code> equals
	 * the given <code>model</code>.</p>
	 * @param model the model to store on the server
	 * @param callback the callback executed on success
	 * @param formCallback the callback executed on validation error
	 * @param failCallback the callback executed on failure, nullable
	 */
	public void store(T model, ModelCallback<T> callback, FormCallback formCallback, FailCallback failCallback) {
		HashMap<String, Object> attributes = getAttributes(model);

		request("", HttpMethod.POST, attributes, new JsonCallback() {

			public void run() {
				if(getStatus() == 201) {
					JsonElement element = getElement();
					if(element != null) {
						if(element.isJsonObject()) {
							model.loadFromJsonObject(element.getAsJsonObject());
							callback.setResponse(getResponse());
							callback.setModel(model);
							callback.run();
						} else handleMalformedResponse(getResponse(), failCallback, "JSON object");
					} else handleMalformedResponse(getResponse(), failCallback, "JSON element but was null");
				} else {
					Logger.getGlobal().warning("Store request returned status " + getStatus() + ", expected 201.");
				}
			}

		}, new FailCallback() {

			public void run() {
				if(getStatus() == 422) { // Validation errors
					formCallback.setResponse(getResponse());
					formCallback.run();
				} else if(failCallback != null) {
					failCallback.setResponse(getResponse());
					failCallback.run();
				}
			}
		});
	}

	/**
	 * <p>Update the given <code>model</code> on the server
	 * based on its id.<p>
	 * <p>All fields given as parameter will be sent for update.
	 * Lists and nested models are ignored.</p>
	 * @param model the model to update on the server
	 * @param formCallback the callback executed on validation error
	 * @param fields the fields to update (in snake case)
	 * @throws IllegalArgumentException thrown if no field is provided
	 */
	public void update(T model, FormCallback formCallback, String ... fields) {
		update(model, null, formCallback, null, fields);
	}

	/**
	 * <p>Update the given <code>model</code> on the server
	 * based on its id.<p>
	 * <p>All fields given as parameter will be sent for update.
	 * Lists and nested models are ignored.</p>
	 * @param model the model to update on the server
	 * @param callback the callback executed on success, nullable
	 * @param formCallback the callback executed on validation error
	 * @param fields the fields to update (in snake case)
	 * @throws IllegalArgumentException thrown if no field is provided
	 */
	public void update(T model, RestCallback callback, FormCallback formCallback, String ... fields) {
		update(model, callback, formCallback, null, fields);
	}

	/**
	 * <p>Update the given <code>model</code> on the server
	 * based on its id.<p>
	 * <p>All fields given as parameter will be sent for update.
	 * Lists and nested models are ignored.</p>
	 * @param model the model to update on the server
	 * @param formCallback the callback executed on validation error
	 * @param fields the fields to update (in snake case)
	 * @throws IllegalArgumentException thrown if no field is provided
	 */
	public void update(T model, FormCallback formCallback, FailCallback failCallback, String ... fields) {
		update(model, null, formCallback, failCallback, fields);
	}

	/**
	 * <p>Update the given <code>model</code> on the server
	 * based on its id.<p>
	 * <p>All fields given as parameter will be sent for update.
	 * Lists and nested models are ignored.</p>
	 * @param model the model to update on the server
	 * @param callback the callback executed on success, nullable
	 * @param formCallback the callback executed on validation error
	 * @param failCallback the callback executed on failure, nullable
	 * @param fields the fields to update (in snake case)
	 * @throws IllegalArgumentException thrown if no field is provided
	 */
	public void update(T model, RestCallback callback, FormCallback formCallback, FailCallback failCallback, String ... fields) {
		if(fields.length == 0) throw new IllegalArgumentException("At least one field must be given to update.");

		var params = new HashMap<String, Object>();
		HashMap<String, Object> attributes = getAttributes(model);

		for(String s : fields) {
			if(attributes.containsKey(s)) {
				params.put(s, attributes.get(s));
			} else {
				Logger.getGlobal().warning("Attribute \"" + s + "\" doesn't exist or is not applicable.");
			}
		}

		request(String.valueOf(model.getId().get()), HttpMethod.PUT, attributes, new RestCallback() {

			public void run() {
				if(getStatus() != 204) {
					Logger.getGlobal().warning("Update request returned status " + getStatus() + ", expected 204.");
				}

				model.setUpdatedAt(new Date());
				if(callback != null) {
					callback.setResponse(getResponse());
					callback.run();
				}
			}

		}, new FailCallback() {

			public void run() {
				if(getStatus() == 422) { // Validation errors
					formCallback.setResponse(getResponse());
					formCallback.run();
				} else if(failCallback != null) {
					failCallback.setResponse(getResponse());
					failCallback.run();
				}
			}

		});
	}

	/**
	 * Destroy a record.
	 * @param record the record to destroy
	 * @param callback the callback executed on success
	 */
	public void destroy(T record, RestCallback callback) {
		destroy(record.getId().get(), callback, null);
	}

	/**
	 * Destroy a record.
	 * @param record the record to destroy
	 * @param callback the callback executed on success
	 * @param failCallback the callback executed on failure
	 */
	public void destroy(T record, RestCallback callback, FailCallback failCallback) {
		destroy(record.getId().get(), callback, failCallback);
	}

	/**
	 * Destroy a record by its id.
	 * @param id the id of the record to destroy
	 * @param callback the callback executed on success
	 */
	public void destroy(int id, RestCallback callback) {
		destroy(id, callback, null);
	}

	/**
	 * Destroy a record by its id.
	 * @param id the id of the record to destroy
	 * @param callback the callback executed on success
	 * @param failCallback the callback executed on failure
	 * @throws IllegalArgumentException thrown if <code>id</code> isn't positive
	 */
	public void destroy(int id, RestCallback callback, FailCallback failCallback) {
		if(id <= 0) throw new IllegalArgumentException("ID must be positive, " + id + " given.");
		request(String.valueOf(id), HttpMethod.DELETE, callback, failCallback);
	}

}
