package fr.elyssif.client.gui.controller;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import fr.elyssif.client.Config;
import javafx.fxml.FXML;

/**
 * Controller for the "send file" view
 * @author Jérémy LAMBERT
 *
 */
public class SendController extends FadeController {

	public void initialize(URL location, ResourceBundle resources) {
		if(Config.getInstance().isVerbose())
			Logger.getGlobal().info("Loading send controller.");
		super.initialize(location, resources);
	}

	@FXML
	public void browseClicked() {
		//
	}

	@FXML
	public void recipientClicked() {
		//
	}

	@FXML
	public void encryptClicked() {

	}

}
