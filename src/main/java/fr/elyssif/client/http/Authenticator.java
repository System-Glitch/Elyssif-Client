package fr.elyssif.client.http;

import java.util.logging.Logger;

import org.apache.http.client.HttpClient;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import fr.elyssif.client.callback.FormCallbackData;
import fr.elyssif.client.callback.RestCallback;
import fr.elyssif.client.callback.RestCallbackData;
import fr.elyssif.client.gui.model.User;
import fr.elyssif.client.gui.notification.NotificationCenter;
import fr.elyssif.client.http.echo.Echo;
import fr.elyssif.client.http.echo.EchoOptions;
import fr.elyssif.client.http.echo.EchoSubscriptionException;
import fr.elyssif.client.http.echo.SocketIOConnector;

/**
 * Utility class for authentication and token holding.
 *
 * @author Jérémy LAMBERT
 */
public final class Authenticator {

	private static final String USER_INFO_ENDPOINT = "/api/user";
	private static final String LOGIN_ENDPOINT = "/api/login";
	private static final String LOGOUT_ENDPOINT = "/api/logout";
	private static final String REGISTER_ENDPOINT = "/api/register";

	private HttpClient client;
	private String token;
	private String host;
	private String socketHost;
	private User user; //The authenticated user
	private Echo echo;
	private NotificationCenter notificationCenter;

	/**
	 * Create a new instance of Authenticator.
	 * 
	 * @param client the client to use for the authentication and user info queries
	 * @param host the url of the host (without trailing slash)
	 * @param socketHost the url of the socket host (without trailing slash)
	 */
	public Authenticator(HttpClient client, String host, String socketHost) {
		this(client, host, socketHost, null);
	}

	/**
	 * Create a new instance of Authenticator.
	 * 
	 * @param client the client to use for the authentication and user info queries
	 * @param host the url of the host (without trailing slash)
	 * @param socketHost the url of the socket host (without trailing slash)
	 * @param token access token if you have it, nullable
	 */
	public Authenticator(HttpClient client, String host, String socketHost, String token) {
		this.client = client;
		this.host = host;
		this.socketHost = socketHost;
		this.token = token;
	}

	private final void initEcho() {

		if(echo == null) {
			EchoOptions options = new EchoOptions();
			options.host = this.socketHost;
			options.headers.put("Authorization", "Bearer " + this.token);

			this.echo = new Echo(options);
		}

		SocketIOConnector.setExiting(false);
		echo.connect(messageSuccess -> {
			// TODO on connect
			notificationCenter = new NotificationCenter(echo);
			notificationCenter.listen("user." + user.getId().get(), "UserNotification");
		}, messageError -> Logger.getGlobal().info("Error"),
		subError -> {
			throw new EchoSubscriptionException(subError);
		});
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
	 * @return user, can be null
	 */
	public final User getUser() {
		return user;
	}

	/**
	 * Get the Echo socket currently in use.
	 * @return echo
	 */
	public final Echo getEcho() {
		return echo;
	}

	/**
	 * Send a login request and store the access token on success.
	 * @param email
	 * @param password
	 * @param callback the callback to execute on success
	 */
	public final void login(String email, String password, RestCallback callback) {
		RestRequest request = new RestRequest(client, host + LOGIN_ENDPOINT)
				.param("email", email.trim())
				.param("password", password);

		request.asyncExecute(HttpMethod.POST, new AuthenticationCallback(callback));
	}

	/**
	 * Send a logout request and destroy the access token.
	 * @param callback
	 */
	public final void logout(RestCallback callback) {
		RestRequest request = new RestRequest(client, host + LOGOUT_ENDPOINT)
				.setAuthorizationToken(token);

		request.asyncExecute(HttpMethod.DELETE, data -> {

			RestResponse response = data.getResponse();
			if(response.getStatus() == 204 || response.getStatus() == 401) {
				token = null;
				user = null;
				if(echo != null) {
					SocketIOConnector.setExiting(true);
					echo.disconnect();
				}
			} else
				Logger.getGlobal().warning("Logout request failed with state " + response.getStatus());

			callback.run(data);

		});
	}

	/**
	 * Send a register request and store the access token on success.
	 * @param email
	 * @param password
	 * @param passwordConfirmation the password confirmation, should be equal to <code>password</code>
	 * @param name the name of the user to register
	 * @param callback
	 */
	public final void register(String email, String password, String passwordConfirmation, String name, RestCallback callback) {
		RestRequest request = new RestRequest(client, host + REGISTER_ENDPOINT)
				.param("email", email.trim())
				.param("password", password)
				.param("password_confirmation", passwordConfirmation)
				.param("name", name.trim());

		request.asyncExecute(HttpMethod.POST, new AuthenticationCallback(callback));
	}

	/**
	 * Make a request to get the current authenticated user's info and store it.
	 * @param callback the callback to execute after the response is received, successful or not. Nullable
	 */
	public final void requestUserInfo(RestCallback callback) {
		RestRequest request = new RestRequest(client, host + USER_INFO_ENDPOINT)
				.setAuthorizationToken(token);

		request.asyncExecute(HttpMethod.GET, data -> {

			RestResponse response = data.getResponse();
			if(response.getStatus() == 200) {
				JsonElement element = response.getJsonElement();
				if(element.isJsonObject()) {
					user = new User(element.getAsJsonObject());
					Logger.getGlobal().info("Authenticated user: " + user.getEmail().get() + " (" + user.getName().get() + ")");
				} else {
					Logger.getGlobal().severe("Malformed user info response. Returned element is not a JSON object: " + response.getRawBody());
				}

				initEcho();
			} else if(response.getStatus() == 401) {
				token = null;
				user = null;
				Logger.getGlobal().warning("Invalid authentication token, deleting.");
			} else
				Logger.getGlobal().warning("User info request failed with state " + response.getStatus());

			if(callback != null) {
				callback.run(new RestCallbackData(response));
			}

		});
	}

	/**
	 * Custom callback for authentication requests.
	 *
	 * @author Jérémy LAMBERT
	 */
	private class AuthenticationCallback implements RestCallback {

		private RestCallback callback;

		AuthenticationCallback(RestCallback callback) {
			this.callback = callback;
		}

		public void run(RestCallbackData data) {
			RestResponse response = data.getResponse();
			if(response.getStatus() == 200) {
				JsonElement json = response.getJsonElement();
				if(json.isJsonObject()) {
					JsonObject object = json.getAsJsonObject();
					if(object.has("token") && object.get("token").isJsonPrimitive()) {
						setToken(object.get("token").getAsString());
						requestUserInfo(null);
					} else {
						Logger.getGlobal().warning("Malformed authentication response: " + response.getRawBody());
						return;
					}
				} else {
					Logger.getGlobal().severe("Malformed authentication response. Returned element is not a JSON object: " + response.getRawBody());
				}
			}

			if(response.getStatus() == 422) {
				callback.run(new FormCallbackData(response));
			} else {
				callback.run(data);
			}
		}

	}

}
