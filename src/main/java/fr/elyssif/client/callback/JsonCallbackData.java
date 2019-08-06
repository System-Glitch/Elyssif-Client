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
