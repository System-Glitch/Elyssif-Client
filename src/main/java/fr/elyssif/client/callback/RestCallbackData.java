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
