package fr.elyssif.client.callback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import fr.elyssif.client.http.RestResponse;

/**
 * Callback data for form submission, handling validation errors.
 * @author Jérémy LAMBERT
 *
 */
public final class FormCallbackData extends RestCallbackData {

	private HashMap<String, ArrayList<String>> validationErrors;

	public FormCallbackData(RestResponse response) {
		super(response);
	}

	public void setResponse(RestResponse response) {
		super.setResponse(response);

		validationErrors = new HashMap<String, ArrayList<String>>();

		//Handle validation errors
		JsonElement json = response.getJsonElement();
		if(json.isJsonObject()) {
			JsonObject object = json.getAsJsonObject();
			if(object.has("errors")) {
				JsonElement errors = object.get("errors");
				if(errors.isJsonObject()) {
					for(Entry<String, JsonElement> entry : errors.getAsJsonObject().entrySet()) {

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
