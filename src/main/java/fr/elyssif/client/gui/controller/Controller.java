package fr.elyssif.client.gui.controller;

import java.lang.reflect.Field;
import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import fr.elyssif.client.gui.view.SlideDirection;
import javafx.animation.Interpolator;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

/**
 * Super-class for controllers. Handles sliding transitions and back buttons.
 * @author Jérémy LAMBERT
 *
 */
public abstract class Controller implements Initializable {

	private static final double SLIDING_DURATION = 500;

	private Controller backController;
	private SlideDirection slideDirection = SlideDirection.NONE;
	private TranslateTransition openTransition;
	private TranslateTransition closeTransition;
	private ResourceBundle bundle;
	private URL location;

	private HashMap<String, Controller> controllers; //Contains child controllers

	@FXML private Pane pane;

	public void initialize(URL location, ResourceBundle resources) {
		this.bundle    = resources;
		this.location  = location;

		registerControllers();

		openTransition = new TranslateTransition(new Duration(SLIDING_DURATION), pane);
		openTransition.setToX(0);
		openTransition.setInterpolator(Interpolator.EASE_OUT);

		closeTransition = new TranslateTransition(new Duration(SLIDING_DURATION), pane);
		closeTransition.setInterpolator(Interpolator.EASE_IN);
		closeTransition.setFromX(0);
		closeTransition.setOnFinished((e) -> backController.show(false));
	}

	/**
	 * Show the according view. Plays the transition if slide direction is not "none" and transition is true.
	 * @param transition - plays transition if true, simply puts pane to front if false
	 */
	public void show(boolean transition) {
		show(transition, null);
		SnackbarController.getInstance().updateZOrder();
	}

	/**
	 * Show the according view. Plays the transition if slide direction is not "none" and transition is true.
	 * @param transition - plays transition if true, simply puts pane to front if false
	 * @param backController - the controller which should be called when the back button is clicked
	 */
	public void show(boolean transition, Controller backController) {
		setBackController(backController);
		pane.toFront();
		SnackbarController.getInstance().updateZOrder();
		if(transition && !slideDirection.equals(SlideDirection.NONE)) {
			if(slideDirection == SlideDirection.VERTICAL)
				openTransition.setFromY(-pane.getHeight()-40);
			else if(slideDirection == SlideDirection.HORIZONTAL)
				openTransition.setFromX(-pane.getWidth()-40);
			openTransition.play();
		}
	}

	/**
	 * Show the previous view. Plays the transition backwards.
	 */
	protected void back() {
		if(slideDirection == SlideDirection.VERTICAL)
			closeTransition.setToY(-pane.getHeight()-40);
		else if(slideDirection == SlideDirection.HORIZONTAL)
			closeTransition.setToX(-pane.getWidth()-40);
		closeTransition.play();
	}

	/**
	 * Get the slide transition direction.
	 * @return slideDirection
	 * @see SlideDirection
	 */
	protected final SlideDirection getSlideDirection() {
		return slideDirection;
	}

	/**
	 * Set the slide transition direction
	 * @param slideDirection
	 */
	protected final void setSlideDirection(SlideDirection slideDirection) {
		this.slideDirection = slideDirection;
		if(slideDirection == SlideDirection.VERTICAL) {
			openTransition.setToY(0);
			openTransition.setToX(0);
			openTransition.setFromX(0);

			closeTransition.setFromY(0);
			closeTransition.setToX(0);
			closeTransition.setFromX(0);
		} else if(slideDirection == SlideDirection.HORIZONTAL) {
			openTransition.setToX(0);
			openTransition.setToY(0);
			openTransition.setFromY(0);

			closeTransition.setFromX(0);
			closeTransition.setToY(0);
			closeTransition.setFromY(0);
		}
	}

	/**
	 * Get the main pane for this controller.
	 * @return pane
	 */
	protected final Pane getPane() {
		return pane;
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
	 * @return backController
	 */
	protected final void setBackController(Controller backController) {
		this.backController = backController;
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
	protected Controller getController(String key) {
		return controllers.get(key);
	}

	/**
	 * Register a child controller.
	 * @param key - the name of the controller
	 * @param controller
	 */
	private void registerController(String key, Controller controller) {
		controllers.put(key, controller);
	}

	/**
	 * Detect and register child controllers.
	 */
	private void registerControllers() {

		controllers = new HashMap<>();

		for(Field field  : getClass().getDeclaredFields()) {
			if (field.isAnnotationPresent(FXML.class) && Controller.class.isAssignableFrom(field.getType())) {
				try {
					String name = field.getName().substring(0, field.getName().lastIndexOf("Controller"));
					field.setAccessible(true);
					registerController(name, (Controller) field.get(this));
					field.setAccessible(false);
				} catch (IllegalArgumentException | IllegalAccessException e) {
					Logger.getGlobal().log(Level.SEVERE, "Error while registering child controllers.", e);
				}
			}
		}
	}

}
