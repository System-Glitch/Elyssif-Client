package fr.elyssif.client.gui.controller;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import fr.elyssif.client.Config;

public final class AppContainerController extends ContainerController {

	public void initialize(URL location, ResourceBundle resources) {
		if(Config.getInstance().isVerbose())
			Logger.getGlobal().info("Loading app container controller.");
		super.initialize(location, resources);
		
		// TODO show default panel
	}
	
}
