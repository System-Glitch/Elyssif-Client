package fr.elyssif.client.gui.controller;

import com.jfoenix.controls.JFXSnackbar;
import com.jfoenix.controls.JFXSnackbar.SnackbarEvent;

import javafx.css.PseudoClass;
import javafx.scene.control.Label;
import javafx.util.Duration;

/**
 * Singleton controller for the snackbar.<br>
 * A snackbar is used to display short messages on the
 * bottom of the window.
 *
 * @author Jérémy LAMBERT
 */
public final class SnackbarController {

	private static SnackbarController instance;
	private JFXSnackbar bar;
	
	private SnackbarController() {}
	
	/**
	 * Set the JFXSnackbar to use. Required at least once.
	 * @param bar
	 */
	public final void setSnackbar(JFXSnackbar bar) {
		this.bar = bar;
	}
	
	/**
	 * Enqueues a message with a duration of 1500 milliseconds.
	 * @param message
	 * @param type
	 */
	public final void message(String message, SnackbarMessageType type) {
		message(message, type, 4000);
	}
	
	/**
	 * Enqueues a message.
	 * @param message
	 * @param type
	 * @param duration
	 */
	public final void message(String message, SnackbarMessageType type, double duration) {
		if(type.getType() == null)
			bar.enqueue(new SnackbarEvent(new Label(message), Duration.millis(duration), null));
		else
			bar.enqueue(new SnackbarEvent(new Label(message), Duration.millis(duration), PseudoClass.getPseudoClass(type.getType())));
	}
	
	/**
	 * Bring back the snackbar to front. Useful when switching pane in StackPane while a message is visible
	 */
	public final void updateZOrder() {
		if(bar != null)
			bar.toFront();
	}
	
	public static final SnackbarController getInstance() {
		if(instance == null) 
			instance = new SnackbarController();
		return instance;
	}
	
	public enum SnackbarMessageType {
		
		STANDARD(),
		INFO("info"),
		SUCCESS("success"),
		ERROR("error");
		
		private String type;

		private SnackbarMessageType() {
			this(null);
		}
		
		private SnackbarMessageType(String type) {
			this.type = type;
		}
		
		protected String getType() {
			return type;
		}
		
	}
	
}
