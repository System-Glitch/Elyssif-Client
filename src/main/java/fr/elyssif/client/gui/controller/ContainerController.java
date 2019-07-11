package fr.elyssif.client.gui.controller;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller abstract class for containers.<br>
 * Used for controllers that are only containers.<br>
 * Automatically re-enables the pane to avoid inner components to be disabled.
 * @author Jérémy LAMBERT
 *
 */
public abstract class ContainerController extends Controller {

	public void initialize(URL location, ResourceBundle resources) {
		super.initialize(location, resources);
		getPane().setDisable(false);
		getPane().setVisible(true);
	}

}
