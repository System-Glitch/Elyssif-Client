package fr.elyssif.client.gui.controller;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import fr.elyssif.client.Config;
import javafx.fxml.FXML;

public final class AppContainerController extends ContainerController {

	@FXML private HomeController homeController;
	@FXML private SendController sendController;
	@FXML private ReceiveController receiveController;

	public void initialize(URL location, ResourceBundle resources) {
		if(Config.getInstance().isVerbose())
			Logger.getGlobal().info("Loading app container controller.");
		super.initialize(location, resources);
	}

}
