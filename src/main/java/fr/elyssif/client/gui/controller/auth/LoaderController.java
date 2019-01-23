package fr.elyssif.client.gui.controller.auth;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import fr.elyssif.client.Config;
import fr.elyssif.client.gui.controller.FadeController;

/**
 * Controller for the loader view
 * @author Jérémy LAMBERT
 *
 */
public final class LoaderController extends FadeController {

	public void initialize(URL location, ResourceBundle resources) {
		if(Config.getInstance().isVerbose())
			Logger.getGlobal().info("Loading loader controller.");
		super.initialize(location, resources);
	}

}
