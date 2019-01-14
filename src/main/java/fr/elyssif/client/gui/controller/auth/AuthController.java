package fr.elyssif.client.gui.controller.auth;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import fr.elyssif.client.Config;
import fr.elyssif.client.gui.controller.Controller;
import fr.elyssif.client.gui.view.SlideDirection;
import javafx.fxml.FXML;

/**
 * Main controller for the auth views
 * @author Jérémy LAMBERT
 *
 */
public final class AuthController extends Controller {

	@FXML private LoginController loginController;
	@FXML private RegisterController registerController;
	@FXML private WelcomeController welcomeController;

	public void initialize(URL location, ResourceBundle resources) {
		if(Config.getInstance().isVerbose())
			Logger.getGlobal().info("Loading auth controller.");
		super.initialize(location, resources);
		setSlideDirection(SlideDirection.NONE);
		registerControllers();
	}

	/**
	 * Register controllers in a HashMap for future use.
	 */
	private void registerControllers() {
		registerController("login", loginController);
		registerController("register", registerController);
		registerController("welcome", welcomeController);
	}

	/**
	 * Show the according view. Plays the transition if slide direction is not "none" and transition is true.
	 * @param transition - plays transition if true, simply puts pane to front if false
	 */
	public void show(boolean transition) {
		super.show(transition);
		welcomeController.show(transition);
	}

	/**
	 * Show the according view. Plays the transition if slide direction is not "none" and transition is true.
	 * @param transition - plays transition if true, simply puts pane to front if false
	 * @param backController - the controller which should be called when the back button is clicked
	 */
	public void show(boolean transition, Controller backController) {
		super.show(transition, backController);
		welcomeController.show(transition);
	}

}
