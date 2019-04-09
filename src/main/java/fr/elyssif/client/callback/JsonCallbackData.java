package fr.elyssif.client.callback;

import com.google.gson.JsonElement;

import fr.elyssif.client.http.RestResponse;

/**
 * Callback data for REST requests returning JSON.
 * @author Jérémy LAMBERT
 *
 */
public class JsonCallbackData extends RestCallbackData {

	private JsonElement response;

	public JsonCallbackData(RestResponse response) {
		super(response);
		setElement(response.getJsonElement());
	}

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
