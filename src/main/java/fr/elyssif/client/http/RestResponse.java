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
 package fr.elyssif.client.http;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.http.Header;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

/**
 * Result of a Rest query built using RestBuilder.
 * 
 * @author Jérémy LAMBERT
 * 
 * @see RestRequest
 * @see HttpRequest
 *
 */
public class RestResponse {

	private int status;
	private HttpResponse response;
	private String raw;
	private JsonElement jsonElement;

	/**
	 * Use this constructor if the HttpRequest failed. Will instantiate a fail result
	 */
	protected RestResponse() {
		status = -1;
		raw = "Request failed.";
	}

	/**
	 * Use this constructor if an exception was thrown when executing the request.
	 * @param message
	 */
	protected RestResponse(String message) {
		status = -1;
		raw = message;
	}

	protected RestResponse(HttpResponse response) {
		this.response = response;
		status = response.getStatusLine().getStatusCode();

		if(status != 204) {
			try {
				raw = EntityUtils.toString(response.getEntity());

				if(raw != null && !raw.isEmpty()) {
					JsonParser parser = new JsonParser();
					jsonElement = parser.parse(raw);
				}
			} catch (UnsupportedOperationException | IOException e) {
				Logger.getGlobal().log(Level.SEVERE, "Unable to read HttpResponse", e);
				status = -1;
				raw = e.getMessage();
			}
		} else {
			raw = null;
		}
	}

	/**
	 * Get if the response is a redirect (http status code 300)
	 * @return if the response is a redirect
	 */
	public boolean isRedirect() {
		return status >= 300 && status < 400 && status != -1;
	}

	/**
	 * Get the success status of the query. Any error status (400, 500, ...) will flag the query as failed.<br>
	 * Not that status 300 will not be considered as successful.
	 * @return the success of the query
	 */
	public boolean isSuccessful() {
		return status < 300 && status != -1;
	}

	/**
	 * Get if the request is invalid.<br>
	 * @return true if the request status is an error 4xx
	 */
	public boolean isInvalid() {
		return status >= 400 && status < 500;
	}

	/**
	 * Get the HTTP status code from the response. A -1 status indicates an error occurred when executing the request.
	 * @return the http status code
	 */
	public int getStatus() {
		return status;
	}

	/**
	 * Get the raw response body
	 * @return raw response
	 */
	public String getRawBody() {
		return raw;
	}

	/**
	 * Get the JsonElement parsed from the raw response
	 * @return the json element parsed from the raw response
	 * @see JsonElement
	 */
	public JsonElement getJsonElement() {
		return jsonElement;
	}

	public String toString() {
		String full = response.getStatusLine().toString();

		for (Header header : response.getAllHeaders()) {
			full += "\n" + header.getName() + ": " + header.getValue();
		}
		if(raw != null) {
			full += "\n\n";
			full += raw;
		}
		return full;
	}
}
