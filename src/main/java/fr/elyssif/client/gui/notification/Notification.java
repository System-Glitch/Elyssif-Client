package fr.elyssif.client.gui.notification;

import java.awt.SystemTray;
import java.awt.TrayIcon.MessageType;
import java.util.logging.Logger;

/**
 * System tray notification
 * @author Jérémy LAMBERT
 *
 */
public class Notification {

	private String title;
	private String message;
	private MessageType type;

	private SystemTray tray;

	/**
	 * Create a new notification.<br>
	 * If the system tray is not supported, nothing will happen on <code>show()</code>.
	 * @param title
	 * @param message
	 * @param type
	 */
	public Notification(String title, String message, MessageType type) {
		if (!SystemTray.isSupported()) {
			Logger.getGlobal().warning("System tray not supported!");
		} else {
			tray = SystemTray.getSystemTray();
			this.title = title;
			this.message = message;
			this.type = type;
		}
	}

	public void show() {
		if(tray != null) {
			tray.getTrayIcons()[0].displayMessage(title, message, type);
		}
	}
	
}
