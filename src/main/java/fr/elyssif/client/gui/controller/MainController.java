package fr.elyssif.client.gui.controller;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import com.jfoenix.controls.JFXSnackbar;

import fr.elyssif.client.Config;
import fr.elyssif.client.gui.controller.SnackbarController.SnackbarMessageType;
import fr.elyssif.client.gui.controller.auth.AuthController;
import javafx.fxml.FXML;

/**
 * Controller for the Main view
 * @author Jérémy LAMBERT
 *
 */
public final class MainController extends Controller {

	private static MainController instance;

	@FXML private HomeController homeController;
	@FXML private AuthController authController;

	public void initialize(URL location, ResourceBundle resources) {
		if(Config.getInstance().isVerbose())
			Logger.getGlobal().info("Loading main controller.");
		super.initialize(location, resources);
		instance = this;

		SnackbarController.getInstance().setSnackbar(new JFXSnackbar(getPane()));
		SnackbarController.getInstance().message("This is a success!", SnackbarMessageType.SUCCESS);
		SnackbarController.getInstance().message("This is an error", SnackbarMessageType.ERROR);
		SnackbarController.getInstance().message("This is some info.", SnackbarMessageType.INFO);
	}

	public static MainController getInstance() {
		return instance;
	}

	/**
	 * Callback when GUI is ready and shown on screen.
	 */
	public void ready() {
		authController.getController("welcome").show(true);
	}

}
