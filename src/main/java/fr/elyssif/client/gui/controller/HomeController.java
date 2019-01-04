package fr.elyssif.client.gui.controller;

import java.util.logging.Logger;

import javafx.fxml.FXML;

/**
 * Controller for the home view.
 * @author Jérémy LAMBERT
 *
 */
public final class HomeController extends Controller {

	@FXML
	protected void initialize() {
		Logger.getGlobal().info("Loading home controller.");
		super.initialize();
	}
	
	@FXML
	private void clicked() {
		MainController.getInstance().getController("login").show(true, this);
	}
}
