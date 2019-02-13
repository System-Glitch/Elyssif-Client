package fr.elyssif.client.gui.repository;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.http.client.HttpClient;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import fr.elyssif.client.Config;
import fr.elyssif.client.gui.controller.LogoutCallback;
import fr.elyssif.client.gui.controller.MainController;
import fr.elyssif.client.gui.model.Model;
import fr.elyssif.client.gui.model.ModelCallback;
import fr.elyssif.client.gui.view.Paginator;
import fr.elyssif.client.http.Authenticator;
import fr.elyssif.client.http.FailCallback;
import fr.elyssif.client.http.HttpMethod;
import fr.elyssif.client.http.JsonCallback;
import fr.elyssif.client.http.PaginateCallback;
import fr.elyssif.client.http.RequestCallback;
import fr.elyssif.client.http.RestCallback;
import fr.elyssif.client.http.RestRequest;
import fr.elyssif.client.http.RestResponse;
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
	private T instantiateReferenceModel() {
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
	private ObservableList<T> parseArray(JsonArray array) {
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
	private void handlePaginateResponse(RestResponse response, PaginateCallback<T> callback, FailCallback failCallback) {
		JsonElement element = response.getJsonElement();
		if(element.isJsonObject()) {

			JsonObject object = element.getAsJsonObject();
			if(object.has("items")) {
				JsonElement itemsElement = object.get("items");
				if(itemsElement.isJsonArray()) {
					ObservableList<T> list = parseArray(itemsElement.getAsJsonArray());
					Paginator<T> paginator = new Paginator<T>(list);

					if(object.has("paginator")) {
						JsonElement paginatorElement = object.get("paginator");
						if(paginatorElement.isJsonObject()) {
							paginator.loadFromJson(paginatorElement.getAsJsonObject());
						} else handleMalformedResponse(response, failCallback, "\"paginator\" attribute to be an object");
					} else {
						Logger.getGlobal().warning("Paginate response doesn't contain paginator. Skip and use default values.");
					}

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
	protected void setAuthenticated(boolean authenticated) {
		this.authenticated = authenticated;
	}

	/**
	 * Get if the repository needs to add an access token to every request.
	 * @return authenticated
	 */
	public boolean isAuthenticated() {
		return authenticated;
	}

	/**
	 * Executes a request and returns the raw result as JSON.
	 * @param action the action (last segment of the url)
	 * @param callback the callback to execute when the request is done, nullable
	 * @param failCallback the callback to execute if the request fails, nullable
	 */
	protected final void request(String action, HttpMethod method, RestCallback callback, FailCallback failCallback) {
		RestRequest request = new RestRequest(httpClient, Config.getInstance().get("Host") + API_URL + model.getResourceName() + "/" + action);

		if(authenticated && authenticator != null) {
			request.setAuthorizationToken(authenticator.getToken());
		}

		request.asyncExecute(method, new RequestCallback() {

			@Override
			public void run() {
				RestResponse response = getResponse();
				if(response != null && response.isSuccessful()) {
					if(callback != null) {

						if(callback instanceof JsonCallback)
							((JsonCallback) callback).setElement(response.getJsonElement());

						callback.run();
					}
				} else {
					if(failCallback != null) {
						failCallback.setResponse(response);
						failCallback.run();
					}
					if(response.getStatus() == 401 && authenticator != null) { // Unauthenticated
						authenticator.logout(new LogoutCallback());
					} else {
						Logger.getGlobal().warning("Repository request failed: " + method.name() + " " + response.getStatus() + " " + failCallback.getMessage());
					}
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
	 * @param failCallback the fail callback to execute
	 * @param expected the name of the expected element
	 */
	protected void handleMalformedResponse(RestResponse response, FailCallback failCallback, String expected) {
		failCallback.setResponse(response);
		failCallback.setMessage("%malformed-response");
		failCallback.run();
		Logger.getGlobal().warning("Malformed response (expected " + expected + "):\n\t" + response.getRawBody());
	}

	// TODO CRUD

	// FETCH (paginated)
	// getwhere

	// create
	// update

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
	 * @param failCallback the callback executed on failure
	 */
	public void getById(int id, ModelCallback<T> callback, FailCallback failCallback) {
		if(id <= 0) throw new IllegalArgumentException("ID must be positive, " + id + " given.");
		request(String.valueOf(id), HttpMethod.GET, new JsonCallback() {

			public void run() {
				T model = instantiateReferenceModel();
				if(model != null) {
					if(getElement().isJsonObject()) {
						model.loadFromJsonObject(getElement().getAsJsonObject());
						callback.setModel(model);
						callback.run();
					} else {
						handleMalformedResponse(getResponse(), failCallback, "JSON object");
					}
				}
			}

		}, failCallback);
	}

	public void getAll(PaginateCallback<T> callback, FailCallback failCallback) {
		request("", HttpMethod.GET, new JsonCallback() { // Empty action for index

			public void run() {
				handlePaginateResponse(getResponse(), callback, failCallback);
			}

		}, failCallback);
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
	 */
	public void destroy(int id, RestCallback callback, FailCallback failCallback) {
		if(id <= 0) throw new IllegalArgumentException("ID must be positive, " + id + " given.");
		request(String.valueOf(id), HttpMethod.DELETE, callback, failCallback);
	}

}
