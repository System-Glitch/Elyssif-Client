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
 package fr.elyssif.client.gui.controller;

import java.lang.reflect.Field;
import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.Pane;
import javafx.stage.Window;

/**
 * Super-class for controllers. Handles sliding transitions and back buttons.
 * @author Jérémy LAMBERT
 *
 */
public abstract class Controller implements Initializable {

	private Controller backController;
	private Controller nextController;
	private Controller parentController;
	private ResourceBundle bundle;
	private URL location;

	private HashMap<String, Controller> controllers; //Contains child controllers

	@FXML private Pane pane;

	public void initialize(URL location, ResourceBundle resources) {
		this.bundle    = resources;
		this.location  = location;

		registerControllers();
		if(pane != null) {
			pane.setDisable(true);
			pane.setVisible(false);
			pane.managedProperty().bind(pane.visibleProperty());
		}
	}

	/**
	 * Show the according view. Plays the transition if slide direction is not "none" and transition is true.
	 * @param transition plays transition if true, simply puts pane to front if false
	 */
	public void show(boolean transition) {
		show(transition, null);
	}

	/**
	 * Show the according view. Plays the transition if slide direction is not "none" and transition is true.
	 * @param transition plays transition if true, simply puts pane to front if false
	 * @param backController the controller which should be called when the back button is clicked
	 */
	protected void show(boolean transition, Controller backController) {
		setBackController(backController);
		if(parentController != null) {
			for(Controller controller : parentController.controllers.values()) {
				if(controller.pane != null) {
					controller.pane.setDisable(true);
					controller.pane.setVisible(false);
				}
			}
		}
		pane.requestFocus();
		pane.toFront();
		pane.setDisable(false);
		pane.setVisible(true);
		SnackbarController.getInstance().updateZOrder();
	}

	/**
	 * Show the previous view. Plays the transition backwards.
	 */
	protected void back() {
		backController.show(false);
	}

	/**
	 * Get the main pane for this controller.
	 * @return the root pane of the view controlled by this controller
	 */
	protected final Pane getPane() {
		return pane;
	}

	/**
	 * Get the parent window.
	 * @return the parent window
	 */
	public Window getWindow() {
		return getPane().getScene().getWindow();
	}

	/**
	 * Get the the parent controller of this controller. Can be null.
	 * @return parentController
	 */
	protected final Controller getParentController() {
		return parentController;
	}

	/**
	 * Get the controller which should be called when the back button is clicked.
	 * @return backController
	 */
	protected final Controller getBackController() {
		return backController;
	}

	/**
	 * Set the controller which should be called when the back button is clicked.
	 */
	protected final void setBackController(Controller backController) {
		this.backController = backController;
	}

	/**
	 * Get the next controller to show.<br>
	 * Used when a transition should be played before the next controller is shown.
	 * @return nextController
	 */
	protected final Controller getNextController() {
		return nextController;
	}

	/**
	 * Set the next controller to show.<br>
	 * Used when a transition should be played before the next controller is shown.
	 */
	protected final void setNextController(Controller nextController) {
		this.nextController = nextController;
	}

	/**
	 * Set the next controller and show it.
	 * @param controller the controller of the next view to show
	 * @param transition
	 */
	public void showNext(Controller controller, boolean transition) {
		setNextController(controller);
		nextController.show(transition, this);
	}

	/**
	 * Show the next controller if it's set.
	 * @param transition
	 */
	public void showNext(boolean transition) {
		if(nextController != null)
			nextController.show(transition, this);
	}

	/**
	 * Get the ResourceBundle for this controller.
	 * @return the ResourceBundle used in the view controlled by this controller
	 * @see ResourceBundle
	 */
	public final ResourceBundle getBundle() {
		return bundle;
	}

	/**
	 * Get the location used to resolve relative paths for the root object, or null if the location is not known.
	 * @return location
	 */
	protected final URL getLocation() {
		return location;
	}


	/**
	 * Get a child controller by its name.
	 * @param key the name of the controller
	 * @return the child controller named with the given key. Null if doesn't exist.
	 * @see Controller
	 */
	public Controller getController(String key) {
		return controllers.get(key);
	}

	/**
	 * Register a child controller.
	 * @param key the name of the controller
	 * @param controller the child controller to register
	 */
	private void registerController(String key, Controller controller) {
		controllers.put(key, controller);
		controller.parentController = this;
	}

	/**
	 * Detect and register child controllers.
	 */
	private void registerControllers() {

		controllers = new HashMap<>();

		for(Field field  : getClass().getDeclaredFields()) {
			if (field.isAnnotationPresent(FXML.class) && Controller.class.isAssignableFrom(field.getType())) {
				try {
					int index = field.getName().lastIndexOf("Controller");
					if(index != -1 && index != 0) { //Exists and doesn't start with
						String name = field.getName().substring(0, index);
						field.setAccessible(true);
						registerController(name, (Controller) field.get(this));
						field.setAccessible(false);
					}
				} catch (IllegalArgumentException | IllegalAccessException e) {
					Logger.getGlobal().log(Level.SEVERE, "Error while registering child controllers.", e);
				}
			}
		}
	}

}
