package fr.elyssif.client.http;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Callback for form submission, handling validation errors.
 * @author Jérémy LAMBERT
 *
 */
public abstract class FormCallback extends RequestCallback implements Runnable {
	
	private HashMap<String, ArrayList<String>> validationErrors;
	
	protected void setResponse(RestResponse response) {
		super.setResponse(response);
		//TODO handle validation errors
	}
	
	/**
	 * Get the validation errors.
	 * @return validatonErrors - null if there is no validation error
	 */
	public HashMap<String, ArrayList<String>> getValidationErrors() {
		return validationErrors;
	}
	
	/**
	 * Check if response has any validation error.
	 * @return boolean
	 */
	public boolean hasValidationErrors() {
		return validationErrors != null;
	}
}
