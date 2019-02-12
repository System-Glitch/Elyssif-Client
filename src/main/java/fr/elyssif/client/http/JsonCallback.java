package fr.elyssif.client.http;

import com.google.gson.JsonObject;

/**
 * Custom Runnable used as callbacks for json REST requests
 * @author Jérémy LAMBERT
 *
 * @see Runnable
 */
public abstract class JsonCallback extends RestCallback {

	private JsonObject response;

	public void setObject(JsonObject response) {
		this.response = response;
	}

	/**
	 * Get the response from the request. All checks have to be done.
	 * @return the response, can be null
	 */
	public JsonObject getObject() {
		return response;
	}

}
