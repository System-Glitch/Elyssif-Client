/*
 * Elyssif-Client
 * Copyright (C) 2019 Jérémy LAMBERT (System-Glitch)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
 package fr.elyssif.client.callback;

import com.google.gson.JsonElement;

import fr.elyssif.client.http.RestResponse;

/**
 * Callback data for failed REST requests.
 * @author Jérémy LAMBERT
 *
 */
public final class FailCallbackData extends RestCallbackData {

	private String message;

	public FailCallbackData(RestResponse response) {
		super(response);
	}

	public void setResponse(RestResponse response) {
		super.setResponse(response);
		loadMessage();
	}

	private void loadMessage() {
		if(getResponse().getStatus() == -1) {
			message = "unreachable";
		} else {
			JsonElement responseElement = getResponse().getJsonElement();
			if(responseElement != null && responseElement.isJsonObject()) {
				JsonElement element = responseElement.getAsJsonObject().get("error");
				if(element != null && element.isJsonPrimitive())
					message = element.getAsString();
				else
					message = "server-error";
			} else
				message = "server-error";
		}
	}

	/**
	 * Get the error message.
	 * @return the message from the response
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * Set the error message.
	 * @param message the error message
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * Get a message that can be directly displayed to the user using the snackbar for example.
	 * @return a built message for the user
	 */
	public String getFullMessage() {
		return !getResponse().isSuccessful() && getStatus() != -1 ? getStatus() + " : " + getMessage() : getMessage();
	}

}
