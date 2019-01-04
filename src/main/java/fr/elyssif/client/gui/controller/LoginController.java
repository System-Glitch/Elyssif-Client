package fr.elyssif.client.gui.controller;

import java.util.logging.Logger;

import fr.elyssif.client.gui.view.SlideDirection;
import javafx.fxml.FXML;

/**
 * Controller for the login view
 * @author Jérémy LAMBERT
 *
 */
public final class LoginController extends Controller {

	@FXML
	protected void initialize() {
		Logger.getGlobal().info("Loading login controller.");
		super.initialize();
		setSlideDirection(SlideDirection.VERTICAL);
	}
	
	@FXML
	private void clickLogin() {
		back();
	}
	
}
