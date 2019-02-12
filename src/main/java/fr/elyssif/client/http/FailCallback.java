package fr.elyssif.client.http;

import com.google.gson.JsonElement;

/**
 * Custom Runnable used as callbacks for failed REST requests
 * @author Jérémy LAMBERT
 *
 * @see Runnable
 */
public abstract class FailCallback extends RestCallback {

	private String message;

	public void setResponse(RestResponse response) {
		super.setResponse(response);
		loadMessage();
	}

	private void loadMessage() {
		if(getResponse().getStatus() == -1) {
			message = "%unreachable";
		} else {
			JsonElement element = getResponse().getJsonObject().get("error");
			if(element != null && element.isJsonPrimitive())
				message = element.getAsString();
			else
				message = "%server-error";
		}
	}

	/**
	 * Get the error message
	 * @return the message from the response
	 */
	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * Get a message that can be directly displayed to the user using the snackbar for example.
	 * @return a built message for the user
	 */
	public String getFullMessage() {
		return getStatus() != 200 && getStatus() != -1 ? getStatus() + " : " + getMessage() : getMessage();
	}

}
