package fr.elyssif.client.gui.repository;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.http.client.HttpClient;

import com.google.gson.JsonObject;

import fr.elyssif.client.Config;
import fr.elyssif.client.gui.controller.MainController;
import fr.elyssif.client.gui.model.Model;
import fr.elyssif.client.http.Authenticator;
import fr.elyssif.client.http.FailCallback;
import fr.elyssif.client.http.HttpMethod;
import fr.elyssif.client.http.JsonCallback;
import fr.elyssif.client.http.RequestCallback;
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

		instantiateReferenceModel();
	}

	/**
	 * Create an instance of the reference model from generic type.
	 */
	private void instantiateReferenceModel() {
		try {
			ParameterizedType superClass = (ParameterizedType) getClass().getGenericSuperclass();

			@SuppressWarnings("unchecked")
			Class<T> type = (Class<T>) superClass.getActualTypeArguments()[0];
			model = type.getConstructor(Integer.class).newInstance(0);
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			Logger.getGlobal().log(Level.SEVERE, "Couldn't instantiate reference Model.", e);
		}
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
	protected final void request(String action, HttpMethod method, JsonCallback callback, FailCallback failCallback) {
		RestRequest request = new RestRequest(httpClient, Config.getInstance().get("host") + API_URL + model.getResourceName() + "/" + action);

		if(authenticated && authenticator != null) {
			request.setAuthorizationToken(authenticator.getToken());
		}

		request.asyncExecute(method, new RequestCallback() {

			@Override
			public void run() {
				RestResponse response = getResponse();
				if(response != null && response.isSuccessful()) {
					JsonObject payload = response.getJsonObject();
					if(callback != null) {
						callback.setObject(payload);
						callback.run();
					}
				} else {
					if(failCallback != null) {
						failCallback.setResponse(response);
						failCallback.run();
					}
					Logger.getGlobal().warning("Repository request failed: " + response.getStatus() + " " + failCallback.getMessage());
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
	// getbyid
	// getwhere

	// create
	// update
	// delete

}
