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
 package fr.elyssif.client.gui.notification;

import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONException;
import org.json.JSONObject;

import fr.elyssif.client.gui.controller.HomeController;
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
						new Notification("Elyssif", message, MainController.getInstance().getWindow()).show();
						((HomeController) MainController.getInstance().getController("app").getController("container").getController("home")).refresh();
					} catch (JSONException e) {
						Logger.getGlobal().log(Level.SEVERE , "Error while reading notification.", e);
					}
				}
			}
		});
	}
}
