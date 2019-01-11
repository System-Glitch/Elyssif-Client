package fr.elyssif.client.gui.controller;

import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import com.jfoenix.controls.JFXSnackbar;

import fr.elyssif.client.Config;
import fr.elyssif.client.gui.controller.SnackbarController.SnackbarMessageType;
import javafx.fxml.FXML;

/**
 * Controller for the Main view
 * @author Jérémy LAMBERT
 *
 */
public final class MainController extends Controller {
	
	private static MainController instance;
	
	private HashMap<String, Controller> controllers;
	
	@FXML private HomeController homeController;
	@FXML private LoginController loginController;
	
	public void initialize(URL location, ResourceBundle resources) {
		if(Config.getInstance().isVerbose())
			Logger.getGlobal().info("Loading main controller.");
		super.initialize(location, resources);
		instance = this;
		registerControllers();
		
		SnackbarController.getInstance().setSnackbar(new JFXSnackbar(getPane()));
		SnackbarController.getInstance().message("This is a success!", SnackbarMessageType.SUCCESS);
		SnackbarController.getInstance().message("This is an error", SnackbarMessageType.ERROR);
		SnackbarController.getInstance().message("This is some info.", SnackbarMessageType.INFO);
	}
	
	/**
	 * Register controllers in a HashMap for future use.
	 */
	private void registerControllers() {
		controllers = new HashMap<>();
		registerController("home", homeController);
		registerController("login", loginController);
	}
	
	/**
	 * Get a controller by its name
	 * @param key - the name of the controller
	 * @return controller
	 * @see Controller
	 */
	protected Controller getController(String key) {
		return controllers.get(key);
	}
	
	/**
	 * Register a controller
	 * @param key - the name of the controller
	 * @param controller
	 */
	protected void registerController(String key, Controller controller) {
		controllers.put(key, controller);
	}
	
	protected static MainController getInstance() {
		return instance;
	}
 	
}
