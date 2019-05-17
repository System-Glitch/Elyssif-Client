package fr.elyssif.client.http.echo.channel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONObject;

import fr.elyssif.client.Config;
import fr.elyssif.client.http.echo.EchoOptions;
import fr.elyssif.client.http.echo.EventFormatter;
import io.socket.client.Ack;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

/**
 * This class represents a Socket.io channel.
 */
public class SocketIOChannel {

	/**
	 * The socket.
	 */
	protected Socket socket;

	/**
	 * The channel name.
	 */
	private String name;

	/**
	 * Echo options.
	 */
	protected EchoOptions options;

	/**
	 * Event formatter.
	 */
	protected EventFormatter formatter;

	/**
	 * Events callback.
	 */
	private Map<String, List<Emitter.Listener>> eventsCallbacks;

	/**
	 * Create a new Socket.IO channel.
	 *
	 * @param socket  the socket
	 * @param name    channel name
	 * @param options Echo options
	 */
	public SocketIOChannel(Socket socket, String name, EchoOptions options) {
		this.socket = socket;
		this.name = name;
		this.options = options;
		this.formatter = new EventFormatter(options.eventNamespace);
		this.eventsCallbacks = new HashMap<>();

		try {
			this.subscribe(null);
		} catch (Exception e) {
			Logger.getGlobal().log(Level.SEVERE, "Couldn't subscribe to channel.", e);
		}

		configureReconnector();
	}

	/**
	 * Subscribe to a Socket.io channel.
	 *
	 * @param callback callback with response from the server
	 * @throws Exception if error when subscribing to the channel.
	 */
	public void subscribe(Ack callback) throws Exception {
		JSONObject object = new JSONObject();
		try {
			object.put("channel", name);
			object.put("auth", options.getAuth());

			if (callback == null) {
				socket.emit("subscribe", object);
			} else {
				socket.emit("subscribe", object, callback);
			}

			if(Config.getInstance().isVerbose()) {
				Logger.getGlobal().info("Subscribe to Echo channel \"" + name +"\".");
			}

		} catch (Exception e) {
			throw new Exception("Cannot subscribe to channel '" + name + "' : " + e.getMessage());
		}
	}

	/**
	 * Unsubscribe from channel and unbind event callbacks.
	 *
	 * @param callback callback with response from the server
	 * @throws Exception if error when unsubscribing to the channel.
	 */
	public void unsubscribe(Emitter.Listener callback) throws Exception {
		unbind();

		JSONObject object = new JSONObject();
		try {
			object.put("channel", name);
			object.put("auth", options.getAuth());

			if (callback == null) {
				socket.emit("unsubscribe", object);
			} else {
				socket.emit("unsubscribe", object, callback);
			}

			if(Config.getInstance().isVerbose()) {
				Logger.getGlobal().info("Unsubscribe from Echo channel \"" + name +"\".");
			}

		} catch (Exception e) {
			throw new Exception("Cannot unsubscribe to channel '" + name + "' : " + e.getMessage());
		}
	}

	public SocketIOChannel listen(String event, Emitter.Listener callback) {
		on(formatter.format(event), callback);

		return this;
	}

	/**
	 * Bind the channel's socket to an event and store the callback.
	 *
	 * @param event    event name
	 * @param callback callback
	 */
	public void on(String event, final Emitter.Listener callback) {
		Emitter.Listener listener = new Emitter.Listener() {
			@Override
			public void call(Object... objects) {
				if (objects.length > 0 && objects[0] instanceof String) {
					String channel = (String) objects[0];

					if (channel.equals(name)) {
						callback.call(objects);
					}
				}
			}
		};

		socket.on(event, listener);
		bind(event, listener);
	}

	/**
	 * Attach a 'reconnect' listener and bind the event.
	 */
	private void configureReconnector() {
		Emitter.Listener callback = new Emitter.Listener() {
			@Override
			public void call(Object... objects) {
				try {
					subscribe(null);
				} catch (Exception e) {
					Logger.getGlobal().log(Level.SEVERE, "Couldn't re-subscribe to channel.", e);
				}
			}
		};

		socket.on(Socket.EVENT_RECONNECT, callback);
		bind(Socket.EVENT_RECONNECT, callback);
	}

	/**
	 * Bind the channel's socket to an event and store the callback.
	 *
	 * @param event    event name
	 * @param callback callback when event is triggered
	 */
	public void bind(String event, Emitter.Listener callback) {
		if (!eventsCallbacks.containsKey(event)) {
			eventsCallbacks.put(event, new ArrayList<Emitter.Listener>());
		}

		eventsCallbacks.get(event).add(callback);
		if(Config.getInstance().isVerbose()) {
			Logger.getGlobal().info("Added callback on Echo channel \"" + name +"\" for event \"" + event + "\".");
		}
	}

	/**
	 * Unbind the channel's socket from all stored event callbacks.
	 */
	public void unbind() {
		Iterator<String> iterator = eventsCallbacks.keySet().iterator();

		if(Config.getInstance().isVerbose()) {
			Logger.getGlobal().info("Unbound callbacks for Echo channel \"" + name +"\".");
		}

		while (iterator.hasNext()) {
			socket.off(iterator.next());
			iterator.remove();
		}
	}

	/**
	 * @return the channel name.
	 */
	public String getName() {
		return name;
	}
}
