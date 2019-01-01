package fr.elyssif.client.gui.controller;

import java.util.logging.Logger;

import com.jfoenix.controls.JFXSnackbar;

import fr.elyssif.client.gui.controller.SnackbarController.SnackbarMessageType;
import javafx.fxml.FXML;
import javafx.scene.layout.StackPane;

public class MainController {

	@FXML private StackPane pane;
	
	@FXML
	private void initialize() {
		Logger.getGlobal().info("Loading main controller.");
		SnackbarController.getInstance().setSnackbar(new JFXSnackbar(pane));
		SnackbarController.getInstance().message("This is a success!", SnackbarMessageType.SUCCESS);
		SnackbarController.getInstance().message("This is an error", SnackbarMessageType.ERROR);
		SnackbarController.getInstance().message("This is some info.", SnackbarMessageType.INFO);
	}
 	
}
