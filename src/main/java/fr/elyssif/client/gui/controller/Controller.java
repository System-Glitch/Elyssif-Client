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
	}

	/**
	 * Show the according view. Plays the transition if slide direction is not "none" and transition is true.
	 * @param transition - plays transition if true, simply puts pane to front if false
	 */
	public void show(boolean transition) {
		show(transition, null);
	}

	/**
	 * Show the according view. Plays the transition if slide direction is not "none" and transition is true.
	 * @param transition - plays transition if true, simply puts pane to front if false
	 * @param backController - the controller which should be called when the back button is clicked
	 */
	protected void show(boolean transition, Controller backController) {
		setBackController(backController);
		pane.toFront();
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
	 * @return pane
	 */
	protected final Pane getPane() {
		return pane;
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
	 * @param controller - the controller of the next view to show
	 * @param transition
	 */
	protected void showNext(Controller controller, boolean transition) {
		setNextController(controller);
		nextController.show(transition, this);
	}

	/**
	 * Get the ResourceBundle for this controller.
	 * @return bundle
	 * @see ResourceBundle
	 */
	protected final ResourceBundle getBundle() {
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
	 * @param key - the name of the controller
	 * @return controller
	 * @see Controller
	 */
	public Controller getController(String key) {
		return controllers.get(key);
	}

	/**
	 * Register a child controller.
	 * @param key - the name of the controller
	 * @param controller
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
