package fr.elyssif.client.http;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.http.Header;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * Result of a Rest query built using RestBuilder.
 * 
 * @author Jérémy LAMBERT
 * 
 * @see RestRequest
 * @see HttpRequest
 *
 */
public class RestResponse {

	private int status;
	private HttpResponse response;
	private String raw;
	private JsonObject jsonObject;
	
	//TODO errors

	/**
	 * Use this constructor if the HttpRequest failed. Will instantiate a fail result
	 */
	protected RestResponse() {
		status = -1;
		raw = "Request failed.";
	}

	protected RestResponse(HttpResponse response) {
		this.response = response;
		status = response.getStatusLine().getStatusCode();

		if(status == 404)
			Logger.getGlobal().log(Level.SEVERE, "Requested URL returned 404", new NotFoundException("Request returned status 404 NOT FOUND"));

		if(status != 204) {
			try {
				raw = EntityUtils.toString(response.getEntity());

				JsonParser parser = new JsonParser();
				jsonObject = parser.parse(raw).getAsJsonObject();
			} catch (UnsupportedOperationException | IOException e) {
				Logger.getGlobal().log(Level.SEVERE, "Unable to read HttpResponse", e);
			}
		} else {
			raw = null;
		}
	}

	/**
	 * Get if the response is a redirect (http status code 300)
	 * @return if the response is a redirect
	 */
	public boolean isRedirect() {
		return status >= 300 && status < 400;
	}

	/**
	 * Get the success status of the query. Any error status (400, 500, ...) will flag the query as failed.
	 * @return the success of the query
	 */
	public boolean isSuccessful() {
		return status < 400;
	}

	/**
	 * Get the HTTP status code from the response. A -1 status indicates an error occurred when executing the request.
	 * @return the http status code
	 */
	public int getStatus() {
		return status;
	}

	/**
	 * Get the raw response body
	 * @return raw response
	 */
	public String getRawBody() {
		return raw;
	}

	/**
	 * Get the JsonObject parsed from the raw response
	 * @return the json object parsed from the raw response
	 * @see JsonObject
	 */
	public JsonObject getJsonObject() {
		return jsonObject;
	}

	public String toString() {
		String full = response.getStatusLine().toString();

		for (Header header : response.getAllHeaders()) {
			full += "\n" + header.getName() + ": " + header.getValue();
		}

		full += "\n\n";
		full += raw;
		return full;
	}
}
