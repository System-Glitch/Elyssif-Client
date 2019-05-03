package fr.elyssif.http.echo;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import fr.elyssif.http.echo.channel.SocketIOChannel;
import fr.elyssif.http.echo.channel.SocketIOPrivateChannel;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

/**
 * This class creates a connector to a Socket.io server.
 */
public class SocketIOConnector {


	private EchoOptions options;
	private Socket socket;

	/**
	 * All of the subscribed channel names.
	 */
	private Map<String, SocketIOChannel> channels;

	/**
	 * Create a new Socket.IO connector.
	 *
	 * @param options options
	 */
	public SocketIOConnector(EchoOptions options) {
		this.options = options;
		channels = new HashMap<>();
	}

	public void connect(Emitter.Listener success, Emitter.Listener error, Emitter.Listener subscriptionError) {
		try {
			socket = IO.socket(this.options.host);
			socket.connect();

			if (success != null) {
				socket.on(Socket.EVENT_CONNECT, success);
			}

			if (error != null) {
				socket.on(Socket.EVENT_CONNECT_ERROR, error);
			}
			
			if (subscriptionError != null) {
				socket.on("subscription_error", subscriptionError);
			}

		} catch (URISyntaxException e) {
			if (error != null) {
				error.call();
			}
		}
	}

	/**
	 * Listen for general event on the socket.
	 *
	 * @param eventName event name
	 * @param callback  callback
	 * @see io.socket.client.Socket list of event types to listen to
	 */
	public void on(String eventName, Emitter.Listener callback) {
		socket.on(eventName, callback);
	}

	/**
	 * Remove all listeners for a general event.
	 *
	 * @param eventName event name
	 */
	public void off(String eventName) {
		socket.off(eventName);
	}

	/**
	 * Listen for an event on a channel.
	 *
	 * @param channel  channel name
	 * @param event    event name
	 * @param callback callback
	 * @return the channel
	 */
	public SocketIOChannel listen(String channel, String event, Emitter.Listener callback) {
		return this.channel(channel).listen(event, callback);
	}

	public SocketIOChannel channel(String channel) {
		if (!channels.containsKey(channel)) {
			channels.put(channel, new SocketIOChannel(socket, channel, options));
		}
		return channels.get(channel);
	}

	public SocketIOChannel privateChannel(String channel) {
		String name = "private-" + channel;

		if (!channels.containsKey(name)) {
			channels.put(name, new SocketIOPrivateChannel(socket, name, options));
		}
		return channels.get(name);
	}

	public void leave(String channel) {
		String privateChannel = "private-" + channel;
		String presenceChannel = "presence-" + channel;

		for (String subscribed : channels.keySet()) {
			if (subscribed.equals(channel) || subscribed.equals(privateChannel) || subscribed.equals(presenceChannel)) {
				try {
					channels.get(subscribed).unsubscribe(null);
				} catch (Exception e) {
					Logger.getGlobal().log(Level.SEVERE, "Couldn't unsubscribe from channel.", e);
				}

				channels.remove(subscribed);
			}
		}
	}

	public boolean isConnected() {
		return socket.connected();
	}

	public void disconnect() {
		for (String subscribed : channels.keySet()) {
			try {
				channels.get(subscribed).unsubscribe(null);
			} catch (Exception e) {
				Logger.getGlobal().log(Level.SEVERE, "Couldn't unsubscribe from channel on disconnect.", e);
			}
		}

		channels.clear();
		socket.disconnect();
	}
}
