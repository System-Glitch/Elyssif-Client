package fr.elyssif.client.http;

/**
 * Super class for REST responses callbacks.
 * @author Jérémy LAMBERT
 *
 */
public abstract class RestCallback implements Runnable {

	private RestResponse response;

	protected void setResponse(RestResponse response) {
		this.response = response;
	}

	/**
	 * Get the response from the request. All checks have to be done.
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
