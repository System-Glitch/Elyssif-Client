package fr.elyssif.client.gui.repository;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.http.client.HttpClient;

import fr.elyssif.client.Config;
import fr.elyssif.client.gui.controller.LogoutCallback;
import fr.elyssif.client.gui.controller.MainController;
import fr.elyssif.client.gui.model.Model;
import fr.elyssif.client.gui.model.ModelCallback;
import fr.elyssif.client.http.Authenticator;
import fr.elyssif.client.http.FailCallback;
import fr.elyssif.client.http.HttpMethod;
import fr.elyssif.client.http.JsonCallback;
import fr.elyssif.client.http.RequestCallback;
import fr.elyssif.client.http.RestCallback;
import fr.elyssif.client.http.RestRequest;
import fr.elyssif.client.http.RestResponse;

/**
 * The <b>Repository Pattern</b> is an addition to the MVC pattern.
 * It fits right between the Controller and the Model so the controller never interacts directly with the Model.
 *
 * The aim is to:
 * - Lighten controllers by moving the query building and logic into the repositories.
 * - Improve readability and maintainability.
 * - Reduce code redundancy as the super-class `Repository` contains most frequent queries.
 *
 * @author Jérémy LAMBERT
 */
public abstract class Repository<T extends Model<T>> {

	private static final String API_URL = "/api/";

	private HttpClient httpClient;
	private Authenticator authenticator;

	private boolean authenticated; // Defines if sent requests use authentication

	private T model; // Empty model for reference

	public Repository() {
		this(MainController.getInstance().getHttpClient(), MainController.getInstance().getAuthenticator());
	}

	public Repository(HttpClient client) {
		this(client, MainController.getInstance().getAuthenticator());
	}

	public Repository(Authenticator authenticator) {
		this(MainController.getInstance().getHttpClient(), authenticator);
	}

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
	 * @param action - the action (last segment of the url)
	 * @param callback - the callback to execute when the request is done, nullable
	 * @param failCallback - the callback to execute if the request fails, nullable
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
							((JsonCallback) callback).setObject(response.getJsonObject());

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
	 * @param response - the response to pass to the fail callback
	 * @param failCallback - the fail callback to execute
	 * @param expected - the name of the expected element
	 */
	protected void handleMalformedResponse(RestResponse response, FailCallback failCallback, String expected) {
		failCallback.setResponse(response);
		failCallback.setMessage("%malformed-response");
		failCallback.run();
		Logger.getGlobal().warning("Malformed paginate response (missing expected \"" + expected + "\"):\n\t" + response.getRawBody());
	}

	// TODO CRUD

	// FETCH (paginated)
	// getAll
	// getwhere

	// create
	// update

	/**
	 * Get a record by its id.
	 * @param id - the id of the requested record, msut be positive
	 * @param callback - the callback executed on success
	 */
	public void getById(int id, ModelCallback<T> callback) {
		getById(id, callback, null);
	}

	/**
	 * Get a record by its id.
	 * @param id - the id of the requested record, msut be positive
	 * @param callback - the callback executed on success
	 * @param failCallback - the callback executed on failure
	 */
	public void getById(int id, ModelCallback<T> callback, FailCallback failCallback) {
		if(id <= 0) throw new IllegalArgumentException("ID must be positive, " + id + " given.");
		request(String.valueOf(id), HttpMethod.GET, new JsonCallback() {

			public void run() {
				T model = instantiateReferenceModel();
				if(model != null) {
					model.loadFromJsonObject(getObject());
					callback.setModel(model);
					callback.run();
				}
			}

		}, failCallback);
	}

	/**
	 * Destroy a record.
	 * @param id - the id of the record to destroy
	 * @param callback - the callback executed on success
	 */
	public void destroy(T record, RestCallback callback) {
		destroy(record.getId().get(), callback, null);
	}

	/**
	 * Destroy a record.
	 * @param id - the id of the record to destroy
	 * @param callback - the callback executed on success
	 * @param failCallback - the callback executed on failure
	 */
	public void destroy(T record, RestCallback callback, FailCallback failCallback) {
		destroy(record.getId().get(), callback, failCallback);
	}

	/**
	 * Destroy a record by its id.
	 * @param id - the id of the record to destroy
	 * @param callback - the callback executed on success
	 */
	public void destroy(int id, RestCallback callback) {
		destroy(id, callback, null);
	}

	/**
	 * Destroy a record by its id.
	 * @param id - the id of the record to destroy
	 * @param callback - the callback executed on success
	 * @param failCallback - the callback executed on failure
	 */
	public void destroy(int id, RestCallback callback, FailCallback failCallback) {
		if(id <= 0) throw new IllegalArgumentException("ID must be positive, " + id + " given.");
		request(String.valueOf(id), HttpMethod.DELETE, callback, failCallback);
	}

}
