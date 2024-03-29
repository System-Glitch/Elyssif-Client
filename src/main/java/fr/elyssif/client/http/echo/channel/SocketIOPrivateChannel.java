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
 package fr.elyssif.client.http.echo.channel;

import org.json.JSONObject;

import fr.elyssif.client.http.echo.EchoOptions;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

/**
 * This class represents a Socket.io private channel.
 */
public class SocketIOPrivateChannel extends SocketIOChannel {

	/**
	 * Create a new Socket.IO private channel.
	 *
	 * @param socket  the socket
	 * @param name    channel name
	 * @param options Echo options
	 */
	public SocketIOPrivateChannel(Socket socket, String name, EchoOptions options) {
		super(socket, name, options);
	}

	/**
	 * Trigger client event on the channel.
	 *
	 * @param event    event name
	 * @param data     data to send
	 * @param callback callback from the server
	 * @return this channel
	 * @throws Exception if error creating the whisper
	 */
	public SocketIOPrivateChannel whisper(String event, JSONObject data, Emitter.Listener callback) throws Exception {

		JSONObject object = new JSONObject();

		try {
			object.put("channel", getName());
			object.put("event", "client-" + event);

			if (data != null) {
				object.put("data", data);
			}

			if (callback != null) {
				socket.emit("client event", object, callback);
			} else {
				socket.emit("client event", object);
			}

		} catch (Exception e) {
			throw new Exception("Cannot whisper o, channel '" + getName() + "' : " + e.getMessage());
		}

		return this;
	}
}
