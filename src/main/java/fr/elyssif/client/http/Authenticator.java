package fr.elyssif.client.http;

import java.util.logging.Logger;

import org.apache.http.client.HttpClient;

import com.google.gson.JsonObject;

import fr.elyssif.client.Config;
import fr.elyssif.client.gui.model.User;

/**
 * Utility class for authentication and token holding.
 * @author Jérémy LAMBERT
 *
 */
public final class Authenticator {

	private static final String USER_INFO_ENDPOINT = "/api/user";
	private static final String LOGIN_ENDPOINT = "/api/login";
	private static final String LOGOUT_ENDPOINT = "/api/logout";
	private static final String REGISTER_ENDPOINT = "/api/register";

	private HttpClient client;
	private String token;
	private String host;
	private User user; //The authenticated user

	/**
	 * Create a new instance of Authenticator.
	 * 
	 * @param client
	 * @param host - the url of the host (without trailing slash)
	 */
	public Authenticator(HttpClient client, String host) {
		this(client, host, null);
	}

	/**
	 * Create a new instance of Authenticator.
	 * 
	 * @param client
	 * @param host - the url of the host (without trailing slash)
	 * @param token - access token if you have it, nullable
	 */
	public Authenticator(HttpClient client, String host, String token) {
		this.client = client;
		this.host = host;
		this.token = token;
	}

	/**
	 * Get the access token
	 * @return token
	 */
	public final String getToken() {
		return token;
	}

	/**
	 * Set the access token.
	 * @param token
	 */
	public final void setToken(String token) {
		this.token = token;
	}

	/**
	 * Get currently authenticated user.
	 * @return user - can be null
	 */
	public final User getUser() {
		return user;
	}

	/**
	 * Send a login request and store the access token on success.
	 * @param email
	 * @param password
	 * @param formCallback
	 */
	public final void login(String email, String password, FormCallback callback) {
		RestRequest request = new RestRequest(client, host + LOGIN_ENDPOINT)
				.param("email", email.trim())
				.param("password", password);

		request.asyncExecute(HttpMethod.POST, new AuthenticationCallback(callback));
	}

	/**
	 * Send a logout request and destroy the access token.
	 * @param callback
	 */
	public final void logout(RequestCallback callback) {
		RestRequest request = new RestRequest(client, host + LOGOUT_ENDPOINT)
				.setAuthorizationToken(token);

		request.asyncExecute(HttpMethod.DELETE, new RequestCallback() {

			public void run() {
				RestResponse response = getResponse();
				if(response.getStatus() == 204) {
					token = null;
					user = null;
				} else
					Logger.getGlobal().warning("Logout request failed with state " + response.getStatus());

				callback.setResponse(response);
				callback.run();
			}

		});
	}

	/**
	 * Send a register request and store the access token on success.
	 * @param email
	 * @param password
	 * @param passwordConfirmation
	 * @param name
	 * @param formCallback
	 */
	public final void register(String email, String password, String passwordConfirmation, String name, FormCallback callback) {
		RestRequest request = new RestRequest(client, host + REGISTER_ENDPOINT)
				.param("email", email.trim())
				.param("password", password)
				.param("password_confirmation", passwordConfirmation)
				.param("name", name.trim());

		request.asyncExecute(HttpMethod.POST, new AuthenticationCallback(callback));
	}

	/**
	 * Make a request to get the current authenticated user's info and store it.
	 * @param callback - nullable
	 */
	public final void requestUserInfo(RequestCallback callback) {
		RestRequest request = new RestRequest(client, host + USER_INFO_ENDPOINT)
				.setAuthorizationToken(token);

		request.asyncExecute(HttpMethod.GET, new RequestCallback() {

			public void run() {
				RestResponse response = getResponse();
				if(response.getStatus() == 200) {
					//TODO load from repository and sanity check
					JsonObject obj = response.getJsonObject();
					user = new User(obj.get("id").getAsInt());
					user.setEmail(obj.get("email").getAsString());
					user.setName(obj.get("name").getAsString());
					Logger.getGlobal().info("Authenticated user: " + user.getEmail().get() + " (" + user.getName().get() + ")");
				} else if(response.getStatus() == 401) {
					Config.getInstance().set("Token", null);
					Config.getInstance().save();
					user = null;
					Logger.getGlobal().warning("Invalid authentication token, deleting.");
				} else
					Logger.getGlobal().warning("User info request failed with state " + response.getStatus());

				if(callback != null) {
					callback.setResponse(response);
					callback.run();
				}
			}

		});
	}

	/**
	 * Custom callback for authentication requests.
	 * @author Jérémy LAMBERT
	 *
	 */
	private class AuthenticationCallback extends RequestCallback {

		private RequestCallback callback;

		AuthenticationCallback(RequestCallback callback) {
			this.callback = callback;
		}

		public void run() {
			RestResponse response = getResponse();
			if(response.getStatus() == 200) {
				JsonObject json = response.getJsonObject();
				if(json.has("token") && json.get("token").isJsonPrimitive()) {
					setToken(json.get("token").getAsString());
					requestUserInfo(null);
				} else {
					Logger.getGlobal().warning("Malformed authentication response: " + response.getRawBody());
					return;
				}
			}
			callback.setResponse(response);
			callback.run();
		}

	}

}
