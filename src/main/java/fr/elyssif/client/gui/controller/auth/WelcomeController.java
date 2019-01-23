package fr.elyssif.client.gui.controller.auth;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import fr.elyssif.client.Config;
import fr.elyssif.client.gui.controller.FadeController;
import javafx.fxml.FXML;

/**
 * Controller for the welcome view
 * @author Jérémy LAMBERT
 *
 */
public final class WelcomeController extends FadeController {

	public void initialize(URL location, ResourceBundle resources) {
		if(Config.getInstance().isVerbose())
			Logger.getGlobal().info("Loading welcome controller.");
		super.initialize(location, resources);
	}

	@FXML
	private void loginClicked() {
		showNext(getParentController().getController("login"), true);
	}

	@FXML
	private void registerClicked() {
		showNext(getParentController().getController("register"), true);
	}

}
