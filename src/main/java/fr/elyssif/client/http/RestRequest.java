package fr.elyssif.client.http;

import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.StringJoiner;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;

import com.google.gson.Gson;

import fr.elyssif.client.Config;
import javafx.application.Platform;

/**
 * Rest chainable request builder.
 * 
 * @author Jérémy LAMBERT
 * 
 * @see RestResponse
 *
 */
public class RestRequest {

	//Used to automate localization
	private static Locale globalLocale = null;

	private String url;
	private String authorizationToken;
	private Locale locale;
	private Hashtable<String, Object> parameters;
	private Hashtable<String, Object> urlParameters;
	private HttpClient client;

	/**
	 * Set locale to use by default for every request.<br>
	 * Locale can still be changed on a request basis using <code>locale()</code>.
	 * @param locale
	 */
	public static final void setGlobalLocale(Locale locale) {
		globalLocale = locale;
	}

	/**
	 * Get the locale used by default for every request.
	 * @return globalLocale
	 */
	public static final Locale getGlobalLocale() {
		return globalLocale;
	}

	/**
	 * Create a new RestRequest instance
	 * @param client
	 * @param url
	 */
	public RestRequest(HttpClient client, String url) {
		this.client = client;
		this.url = url;
		this.parameters = new Hashtable<>();
		this.urlParameters = new Hashtable<>();
	}

	/**
	 * Create a new RestRequest instance without providing an URL. Therefore, it must be given later using the url() method.
	 * @param client
	 */
	public RestRequest(HttpClient client) {
		this.client = client;
	}

	/**
	 * Set the request URL
	 * @param url
	 * @return current instance, used to chain the builder
	 */
	public RestRequest url(String url) {
		this.url = url;
		return this;
	}

	/**
	 * Get the request URL
	 * @return the request URL
	 */
	public final String getUrl() {
		return url;
	}

	/**
	 * Puts a parameter to the request. Overrides if a value with the given name already exists
	 * @param name the name of the parameter
	 * @param value the value associated with the given name, should implement Serializable
	 * @return current instance, used to chain the builder
	 * @see Serializable
	 */
	public final RestRequest param(String name, Object value) {
		parameters.put(name, value);
		return this;
	}

	/**
	 * Get a read-only Hashtable of the parameters of the request.
	 * @return parameters
	 */
	public final Hashtable<? extends String, ? extends Object> getParameters() {
		return parameters;
	}

	/**
	 * Puts a URL parameter to the request. Overrides if a value with the given name already exists
	 * @param name the name of the parameter
	 * @param value the value associated with the given name, should implement Serializable
	 * @return current instance, used to chain the builder
	 * @see Serializable
	 */
	public final RestRequest urlParam(String name, Object value) {
		urlParameters.put(name, value);
		return this;
	}

	/**
	 * Get a read-only Hashtable of the URL parameters of the request.
	 * @return urlParameters
	 */
	public final Hashtable<? extends String, ? extends Object> getUrlParameters() {
		return urlParameters;
	}

	/**
	 * Set the Locale used for this request.<br>
	 * If not set to null, this will add a "Accept-Language" header to the request.
	 * @param locale
	 * @return current instance, used to chain the builder
	 */
	public final RestRequest locale(Locale locale) {
		this.locale = locale;
		return this;
	}

	/**
	 * Get the currently used Locale.
	 * @return locale
	 * @see Locale
	 */
	public final Locale getLocale() {
		return locale;
	}

	/**
	 * Get the authorization token for this request
	 * @return the authorization token
	 */
	public final String getAuthorizationToken() {
		return authorizationToken;
	}

	/**
	 * Set the request authorization token
	 * @param authorizationToken
	 * @return current instance, used to chain the builder
	 */
	public final RestRequest setAuthorizationToken(String authorizationToken) {
		this.authorizationToken = authorizationToken;
		return this;
	}

	/**
	 * Execute the request asynchronously using the given HttpMethod and executes the given callback when done
	 * 
	 * @param method the Http method to use for this request
	 * @param callback the action to execute when the request is done
	 * 
	 * @see RestResponse
	 * @see HttpMethod
	 * @see RequestCallback
	 */
	public void asyncExecute(HttpMethod method, RequestCallback callback) {

		Thread thread = new Thread(() -> {

			RestResponse result = null;
			try {
				HttpRequestBase request = prepareRequest(method);

				if(request != null) {
					if(Config.getInstance().isVerbose())
						Logger.getGlobal().info("Send request:\n" + requestToString(request));
					HttpResponse response = client.execute(request);
					result = new RestResponse(response);
					if(Config.getInstance().isVerbose())
						Logger.getGlobal().info("Response received:\n" + result.toString());
				}

			} catch (IOException e) {
				Logger.getGlobal().log(Level.SEVERE, "Unable to execute Rest " + method.name() + " request", e);
				result = new RestResponse(e.getMessage()); //Create a response with status code -1 and exception message.
			}

			callback.setResponse(result);
			Platform.runLater(callback);
		});
		thread.setDaemon(true);
		thread.start();
	}

	/**
	 * Prepare the request by instantiating it, setting the headers and the parameters.
	 * @param method The HttpMethod used
	 * @return the prepared request, ready to be executed. null if an error occurred
	 * @see HttpMethod
	 */
	private HttpRequestBase prepareRequest(HttpMethod method) {
		try {
			HttpRequestBase request = method.instantiate(url);

			//Headers
			request.addHeader("Accept", "application/json");
			request.addHeader("Content-type", "application/json; charset=UTF-8");

			if(locale != null)
				request.addHeader("Accept-Language", locale.getLanguage());
			else if(globalLocale != null)
				request.addHeader("Accept-Language", globalLocale.getLanguage());

			if(authorizationToken != null)
				request.addHeader("Authorization", "Bearer " + authorizationToken);

			//Parameters
			if(parameters.size() > 0) {
				request.setURI(new URI(url + urlEncodeParameters()));
				if(!method.equals(HttpMethod.GET))
					((HttpEntityEnclosingRequestBase) request).setEntity(serializeParameters());
				else if(parameters.size() > 0)
					Logger.getGlobal().warning("GET request has " + parameters.size() + " non-URL parameters.");
			}

			return request;
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException | UnsupportedEncodingException | URISyntaxException e) {
			Logger.getGlobal().log(Level.SEVERE, "Unable to instantiate Http request.", e);
		}
		return null;
	}

	/**
	 * Serialize the parameters in json
	 * @return the json string representing parameters
	 * @throws UnsupportedEncodingException 
	 * @see StringEntity
	 */
	private StringEntity serializeParameters() throws UnsupportedEncodingException {
		Gson gson = new Gson();
		StringEntity jsonparam = new StringEntity(gson.toJson(parameters), "UTF-8");
		jsonparam.setContentType("application/json");
		jsonparam.setChunked(true);
		return jsonparam;
	}

	/**
	 * URL encode the parameters and add the question mark at the beginning of the string
	 * @return the URL encoded string representing parameters
	 * @throws UnsupportedEncodingException 
	 */
	private final String urlEncodeParameters() throws UnsupportedEncodingException {
		StringJoiner builder = new StringJoiner("&");
		for(Entry<String, Object> entry : urlParameters.entrySet()) {
			builder.add(entry.getKey() + "=" + URLEncoder.encode(String.valueOf(entry.getValue()), "UTF-8"));
		}
		return "?" + builder.toString();
	}

	/**
	 * Convert an HTTP request to String.
	 * @param request
	 * @return requestString
	 */
	private String requestToString(HttpRequestBase request) {
		try {
			String full = request.toString();

			for (Header header : request.getAllHeaders()) {
				full += "\n" + header.getName() + ": " + header.getValue();
			}
			if(!request.getMethod().equals(HttpMethod.GET.name()) && !request.getMethod().equals(HttpMethod.DELETE.name())) {
				full += "\n\n";
				full += EntityUtils.toString(((HttpEntityEnclosingRequestBase) request).getEntity());
			}
			return full;
		} catch (ParseException | IOException e) {
			Logger.getGlobal().log(Level.SEVERE, "Error while converting HTTP request to string", e);
		}

		return null;
	}

}
