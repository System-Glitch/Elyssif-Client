package fr.elyssif.client.http;

/**
 * Custom Runnable used as callbacks for REST requests
 * @author Jérémy LAMBERT
 *
 * @see Runnable
 */
public abstract class RequestCallback implements Runnable {

	private RestResponse response;

	protected void setResponse(RestResponse response) {
		this.response = response;
	}

	/**
	 * Get the response from the request. All checks have to be done.
	 * @return the response, cannot be null
	 */
	public RestResponse getResponse() {
		return response;
	}

}	
