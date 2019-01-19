package fr.elyssif.client.http;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * Callback for form submission, handling validation errors.
 * @author Jérémy LAMBERT
 *
 */
public abstract class FormCallback extends RequestCallback implements Runnable {

	private HashMap<String, ArrayList<String>> validationErrors;

	public FormCallback() {
		validationErrors = new HashMap<String, ArrayList<String>>();
	}
	
	protected void setResponse(RestResponse response) {
		super.setResponse(response);

		//Handle validation errors
		JsonObject json = response.getJsonObject();
		if(json.has("errors")) {
			JsonElement errors = json.get("errors");
			if(errors.isJsonObject()) {
				for(Entry<String, JsonElement> entry : ((JsonObject) errors).entrySet()) {

					if(entry.getValue().isJsonArray()) { //Store all messages for a single entry
						JsonArray array = entry.getValue().getAsJsonArray();
						var messages = new ArrayList<String>();
						for(JsonElement message : array) {
							if(message.isJsonPrimitive())
								messages.add(message.getAsString());
						}
						validationErrors.put(entry.getKey(), messages);

					}

				}
			}
		}
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
