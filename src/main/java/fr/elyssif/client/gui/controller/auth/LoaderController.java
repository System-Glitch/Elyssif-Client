package fr.elyssif.client.gui.controller.auth;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import com.jfoenix.controls.JFXSpinner;

import fr.elyssif.client.Config;
import fr.elyssif.client.gui.controller.Controller;
import fr.elyssif.client.gui.controller.FadeController;
import javafx.fxml.FXML;

/**
 * Controller for the loader view
 * @author Jérémy LAMBERT
 *
 */
public final class LoaderController extends FadeController {

	@FXML
	private JFXSpinner spinner;

	public void initialize(URL location, ResourceBundle resources) {
		if(Config.getInstance().isVerbose())
			Logger.getGlobal().info("Loading loader controller.");
		super.initialize(location, resources);

		spinner.setVisible(false);
	}

	/**
	 * {@inheritDoc}
	 */
	protected void show(boolean transition, Controller backController) {
		super.show(transition, backController);
		spinner.setVisible(true);
	}

	@Override
	public void onHide() {
		super.onHide();
		spinner.setVisible(false);
	}

}
