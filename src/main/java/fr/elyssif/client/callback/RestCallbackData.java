package fr.elyssif.client.callback;

import fr.elyssif.client.http.RestResponse;

/**
 * Super class for REST responses callbacks info.
 * Contains the needed information for correct handling
 * of the result of a REST call.
 *
 * Usually wrapped into a <code>RestCallback</code>
 * @author Jérémy LAMBERT
 *
 * @see RestCallback
 */
public class RestCallbackData {

	private RestResponse response;

	public RestCallbackData(RestResponse response) {
		setResponse(response);
	}

	/**
	 * Set the response for this callback.
	 * @param response the response, nullable
	 */
	public void setResponse(RestResponse response) {
		this.response = response;
	}

	/**
	 * Get the response from the request. All checks have to be done.
	 * (Such as <code>isSuccessful()</code>)
	 * @return the response, can be null
	 */
	public RestResponse getResponse() {
		return response;
	}

	/**
	 * Get the response's status
	 * @return the response's status
	 */
	public int getStatus() {
		return response.getStatus();
	}

}
