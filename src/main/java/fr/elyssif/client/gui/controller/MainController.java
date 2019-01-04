package fr.elyssif.client.gui.controller;

import java.util.logging.Logger;

import com.jfoenix.controls.JFXSnackbar;

import fr.elyssif.client.gui.controller.SnackbarController.SnackbarMessageType;
import javafx.fxml.FXML;

/**
 * Controller for the Main view
 * @author Jérémy LAMBERT
 *
 */
public class MainController extends Controller {
	
	@FXML
	protected void initialize() {
		super.initialize();
		Logger.getGlobal().info("Loading main controller.");
		SnackbarController.getInstance().setSnackbar(new JFXSnackbar(getPane()));
		SnackbarController.getInstance().message("This is a success!", SnackbarMessageType.SUCCESS);
		SnackbarController.getInstance().message("This is an error", SnackbarMessageType.ERROR);
		SnackbarController.getInstance().message("This is some info.", SnackbarMessageType.INFO);
	}
 	
}
