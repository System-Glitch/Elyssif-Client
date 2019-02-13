package fr.elyssif.client.http;

import com.google.gson.JsonElement;

/**
 * Custom Runnable used as callbacks for json REST requests.
 * @author Jérémy LAMBERT
 *
 * @see Runnable
 */
public abstract class JsonCallback extends RestCallback {

	private JsonElement response;

	/**
	 * Set the json response.
	 * @param response the parsed json, nullable
	 */
	public void setElement(JsonElement response) {
		this.response = response;
	}

	/**
	 * Get the json response from the request. The returned element have not been sanity checked.
	 * @return the response, can be null
	 */
	public JsonElement getElement() {
		return response;
	}

}
