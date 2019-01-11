package fr.elyssif.client.gui.controller;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import fr.elyssif.client.Config;
import fr.elyssif.client.gui.view.SlideDirection;
import javafx.fxml.FXML;

/**
 * Controller for the login view
 * @author Jérémy LAMBERT
 *
 */
public final class LoginController extends Controller {

	public void initialize(URL location, ResourceBundle resources) {
		if(Config.getInstance().get("Verbose").equals("true"))
			Logger.getGlobal().info("Loading login controller.");
		super.initialize(location, resources);
		setSlideDirection(SlideDirection.VERTICAL);
	}
	
	@FXML
	private void clickLogin() {
		back();
	}
	
}
