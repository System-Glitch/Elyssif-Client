package fr.elyssif.client.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;

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
	private String raw;
	private JsonObject jsonObject;

	/**
	 * Use this constructor if the HttpRequest failed. Will instantiate a fail result
	 */
	protected RestResponse() {
		status = -1;
		raw = "Request failed.";
	}

	protected RestResponse(HttpResponse response) {
		status = response.getStatusLine().getStatusCode();

		if(status != 204) {
			try {
				BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
				String line;
				StringBuffer resultStr = new StringBuffer();
				while ((line = rd.readLine()) != null) {
					resultStr.append(line);
				}
				raw = resultStr.toString();

				JsonParser parser = new JsonParser();
				jsonObject = parser.parse(raw).getAsJsonObject();
			} catch (UnsupportedOperationException | IOException e) {
				Logger.getGlobal().log(Level.SEVERE, "Unable to read HttpResponse", e);
			}
		} else {
			raw = null;
		}

		if(status == 404)
			Logger.getGlobal().log(Level.SEVERE, "Requested URL returned 404", new NotFoundException("Request returned status 404 NOT FOUND"));
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
	 * Get the raw response
	 * @return raw response
	 */
	public String getRaw() {
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
}
