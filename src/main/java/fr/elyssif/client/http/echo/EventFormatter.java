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
 package fr.elyssif.client.http.echo;

/**
 * Event name formatter.
 */
public final class EventFormatter {

	/**
	 * Event namespace.
	 */
	private String namespace;

	/**
	 * Creates a new event formatter with an empty namespace.
	 */
	public EventFormatter() {
	}

	/**
	 * Creates a new event formatter.
	 *
	 * @param namespace namespace of events
	 */
	public EventFormatter(String namespace) {
		this.namespace = namespace;
	}

	/**
	 * Format the given event name.
	 *
	 * @param event event name
	 * @return event name formatted
	 */
	public String format(String event) {
		String result = event;

		if (result.charAt(0) == '.' || result.charAt(0) == '\\') {
			return result.substring(1);
		} else if (!namespace.isEmpty()) {
			result = namespace + "." + event;
		}

		return result.replace('.', '\\');
	}

	/**
	 * Sets the namespace.
	 *
	 * @param namespace namespace of events
	 */
	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}
}
