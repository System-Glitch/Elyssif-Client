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
 * Exception thrown on Echo channel subscription error.
 * @author Jérémy LAMBERT
 *
 */
public final class EchoSubscriptionException extends RuntimeException {

	private static final long serialVersionUID = -6342240883329521924L;
	private String channel;
	private Object response;

	public EchoSubscriptionException(Object[] response) {
		super("Echo subscription error: channel \"" + String.valueOf(response[0]) + "\" | response: " + String.valueOf(response[1]));
		channel = String.valueOf(response[0]);
		this.response = response[1];
	}

	public final String getChannel() {
		return channel;
	}

	public final Object getResponse() {
		return response;
	}

}
