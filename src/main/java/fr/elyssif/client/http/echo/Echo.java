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

import fr.elyssif.client.http.echo.channel.SocketIOChannel;
import fr.elyssif.client.http.echo.channel.SocketIOPrivateChannel;
import io.socket.emitter.Emitter;

/**
 * This class is the primary API for interacting with broadcasting.
 */
public final class Echo {

	/**
	 * The broadcasting connector.
	 */
	private SocketIOConnector connector;

	/**
	 * Creates a new Echo instance with default options.
	 */
	public Echo() {
		this(new EchoOptions());
	}

	/**
	 * Create a new Echo instance.
	 *
	 * @param options options
	 */
	public Echo(EchoOptions options) {
		this.connector = new SocketIOConnector(options);
	}

	/**
	 * Connect to the Echo server.
	 *
	 * @param success success callback
	 * @param error error callback
	 * @param subscriptionError callback
	 */
	public void connect(Emitter.Listener success, Emitter.Listener error, Emitter.Listener subscriptionError) {
		connector.connect(success, error, subscriptionError);
	}

	/**
	 * Listen for general event on the socket.
	 *
	 * @param eventName event name
	 * @param callback  callback
	 * @see io.socket.client.Socket list of event types to listen to
	 */
	public void on(String eventName, Emitter.Listener callback) {
		connector.on(eventName, callback);
	}

	/**
	 * Remove all listeners for a general event.
	 *
	 * @param eventName event name
	 */
	public void off(String eventName) {
		connector.off(eventName);
	}

	/**
	 * Listen for an event on a channel instance.
	 *
	 * @param channel  channel name
	 * @param event    event name
	 * @param callback callback when event is triggered
	 * @return the channel
	 */
	public SocketIOChannel listen(String channel, String event, Emitter.Listener callback) {
		return connector.listen(channel, event, callback);
	}

	/**
	 * Get a channel by name.
	 *
	 * @param channel channel name
	 * @return the channel
	 */
	public SocketIOChannel channel(String channel) {
		return (SocketIOChannel) connector.channel(channel);
	}

	/**
	 * Get a private channel by name.
	 *
	 * @param channel channel name
	 * @return the channel
	 */
	public SocketIOPrivateChannel privateChannel(String channel) {
		return (SocketIOPrivateChannel) connector.privateChannel(channel);
	}

	/**
	 * Leave the given channel.
	 *
	 * @param channel channel name
	 */
	public void leave(String channel) {
		connector.leave(channel);
	}

	/**
	 * @return if currently connected to server.
	 */
	public boolean isConnected() {
		return connector.isConnected();
	}

	/**
	 * Disconnect from the Echo server.
	 */
	public void disconnect() {
		connector.disconnect();
	}
}
