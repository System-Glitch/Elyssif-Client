package fr.elyssif.client.gui.controller.auth;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import fr.elyssif.client.Config;
import fr.elyssif.client.gui.controller.Controller;
import fr.elyssif.client.gui.view.SlideDirection;

/**
 * Controller for the welcome view
 * @author Jérémy LAMBERT
 *
 */
public final class WelcomeController extends Controller {

	public void initialize(URL location, ResourceBundle resources) {
		if(Config.getInstance().isVerbose())
			Logger.getGlobal().info("Loading welcome controller.");
		super.initialize(location, resources);
		setSlideDirection(SlideDirection.NONE);
	}
	
}
