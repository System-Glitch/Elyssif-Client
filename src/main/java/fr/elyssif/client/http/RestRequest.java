package fr.elyssif.client.http;

import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.Hashtable;
import java.util.Map.Entry;
import java.util.StringJoiner;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;

import com.google.gson.Gson;

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

	private String url;
	private boolean urlParam;
	private Hashtable<String, Object> parameters;
	private HttpClient client;

	/**
	 * Create a new RestRequest instance
	 * @param client
	 * @param url
	 */
	public RestRequest(HttpClient client, String url) {
		this.client = client;
		this.url = url;
		this.urlParam = false;
		this.parameters = new Hashtable<>();
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
	public String getUrl() {
		return url;
	}

	/**
	 * Puts a parameter to the request. Overrides if a value with the given name already exists
	 * @param name - the name of the parameter
	 * @param value - the value associated with the given name, should implement Serializable
	 * @return current instance, used to chain the builder
	 * @see Serializable
	 */
	public RestRequest param(String name, Object value) {
		parameters.put(name, value);
		return this;
	}

	/**
	 * Define if the request's parameters should be URL parameters or in the body content.
	 * @param urlParam
	 * @return current instance, used to chain the builder
	 */
	public RestRequest setUrlParam(boolean urlParam) {
		this.urlParam = urlParam;
		return this;
	}

	/**
	 * Get if the request's parameters should be URL parameters or in the body content.
	 * @return urlParam
	 */
	public boolean isUrlParam() {
		return urlParam;
	}

	/**
	 * Execute the request asynchronously using the given HttpMethod and executes the given callback when done
	 * 
	 * @param method - the Http method to use for this request
	 * @param callback - the action to execute when the request is done
	 * @return the result of the request
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
					HttpResponse response = client.execute(request);
					result = new RestResponse(response);
				}

			} catch (IOException e) {
				Logger.getGlobal().log(Level.SEVERE, "Unable to execute Rest " + method.name() + " request", e);
				result = new RestResponse(); //Create a response with status code -1 and message "Request failed".
			}

			callback.setResponse(result);
			Platform.runLater(callback);
		});
		thread.setDaemon(true);
		thread.start();
	}

	/**
	 * Prepare the request by instantiating it, setting the headers and the parameters.
	 * @param method - The HttpMethod used
	 * @return the prepared request, ready to be executed. null if an error occurred
	 * @see HttpMethod
	 */
	private HttpRequestBase prepareRequest(HttpMethod method) {
		try {
			HttpRequestBase request = method.instantiate(url);

			//Headers
			request.addHeader("Accept", "application/json");
			request.addHeader("Content-type", "application/json; charset=UTF-8");

			//Parameters
			if(parameters.size() > 0) {
				if(isUrlParam())
					request.setURI(new URI(url + urlEncodeParameters()));
				else
					((HttpEntityEnclosingRequestBase) request).setEntity(serializeParameters());
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
	private String urlEncodeParameters() throws UnsupportedEncodingException {
		StringJoiner builder = new StringJoiner("&");
		for(Entry<String, Object> entry : parameters.entrySet()) {
			builder.add(entry.getKey() + "=" + URLEncoder.encode(String.valueOf(entry.getValue()), "UTF-8"));
		}
		return "?" + builder.toString();
	}

}
