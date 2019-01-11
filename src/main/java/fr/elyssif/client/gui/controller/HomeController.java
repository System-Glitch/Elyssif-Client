package fr.elyssif.client.gui.controller;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import fr.elyssif.client.Config;
import javafx.fxml.FXML;

/**
 * Controller for the home view.
 * @author Jérémy LAMBERT
 *
 */
public final class HomeController extends Controller {

	public void initialize(URL location, ResourceBundle resources) {
		if(Config.getInstance().get("Verbose").equals("true"))
			Logger.getGlobal().info("Loading home controller.");
		super.initialize(location, resources);
	}
	
	@FXML
	private void clicked() {
		MainController.getInstance().getController("login").show(true, this);
	}
}
