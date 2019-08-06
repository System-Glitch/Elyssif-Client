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
 /*
 * EchoOptions.java
 * MrBin99 © 2018
 */
package fr.elyssif.client.http.echo;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Echo options.
 */
public final class EchoOptions {

	/**
	 * Host of the Echo server.<br>
	 * <b>Default : </b>http://localhost:6001
	 */
	public String host;

	/**
	 * Host endpoint.<br>
	 * <b>Default : </b>/api/broadcasting/auth
	 */
	public String authEndpoint;

	/**
	 * Event namespace.<br>
	 * <b>Default : </b>App.Events
	 */
	public String eventNamespace;

	/**
	 * Request headers.
	 */
	public Map<String, String> headers;

	/**
	 * Create default object of options.
	 */
	public EchoOptions() {
		headers = new HashMap<>();
		host = "http://localhost:6001";
		authEndpoint = "/api/broadcasting/auth";
		eventNamespace = "App.Events";
	}

	/**
	 * @return the auth JSON object.
	 * @throws Exception if error creating the JSON.
	 */
	public JSONObject getAuth() throws Exception {
		JSONObject auth = new JSONObject();
		JSONObject headers = new JSONObject();

		for (String header : this.headers.keySet()) {
			headers.put(header, this.headers.get(header));
		}

		auth.put("headers", headers);

		return auth;
	}
}
