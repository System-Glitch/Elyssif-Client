package fr.elyssif.client.gui.notification;

import java.awt.TrayIcon.MessageType;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONException;
import org.json.JSONObject;

import fr.elyssif.client.gui.controller.MainController;
import fr.elyssif.client.http.echo.Echo;
import fr.elyssif.client.http.echo.channel.SocketIOPrivateChannel;

/**
 * Listen to notification channels and display
 * system tray notifications.
 * @author Jérémy LAMBERT
 *
 */
public class NotificationCenter {

	private Echo echo;

	public NotificationCenter(Echo echo) {
		this.echo = echo;
	}

	/**
	 * Listen a notification channel.
	 * @param channel
	 * @param eventName
	 */
	public void listen(String channel, String eventName) {
		SocketIOPrivateChannel privateChannel = echo.privateChannel(channel);
		privateChannel.listen(eventName, args -> {
			if(args.length >= 2) {
				JSONObject obj = (JSONObject) args[1];
				if(obj.has("message")) {
					try {
						String message = (String) obj.get("message");
						ResourceBundle bundle = MainController.getInstance().getBundle();
						if(message.startsWith("%")) {
							message = message.replaceFirst("%", "");
							if(bundle.containsKey(message)) {
								message = bundle.getString(message);
								message = message.replace("%PLACEHOLDER%", obj.has("value") ? (String) obj.get("value") : "");								
							}
						}
						new Notification("Elyssif", message, MessageType.NONE).show();
					} catch (JSONException e) {
						Logger.getGlobal().log(Level.SEVERE , "Error while reading notification.", e);
					}						
				}
			}
		});
	}

}
