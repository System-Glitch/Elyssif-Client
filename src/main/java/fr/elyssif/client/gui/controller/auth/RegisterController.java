package fr.elyssif.client.gui.controller.auth;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import fr.elyssif.client.Config;
import fr.elyssif.client.gui.controller.Controller;
import fr.elyssif.client.gui.view.SlideDirection;
import javafx.fxml.FXML;

/**
 * Controller for the register view
 * @author Jérémy LAMBERT
 *
 */
public final class RegisterController extends Controller {

	public void initialize(URL location, ResourceBundle resources) {
		if(Config.getInstance().isVerbose())
			Logger.getGlobal().info("Loading register controller.");
		super.initialize(location, resources);
		setSlideDirection(SlideDirection.NONE);
	}
	
	@FXML
	private void clickBack() {
		back();
	}
	
}
