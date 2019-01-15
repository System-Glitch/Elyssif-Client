package fr.elyssif.client.gui.controller.auth;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;

import fr.elyssif.client.Config;
import fr.elyssif.client.gui.controller.FadeController;
import fr.elyssif.client.gui.controller.MainController;
import fr.elyssif.client.http.Authenticator;
import fr.elyssif.client.http.FormCallback;
import javafx.fxml.FXML;

/**
 * Controller for the register view
 * @author Jérémy LAMBERT
 *
 */
public final class RegisterController extends FadeController {

	@FXML JFXTextField emailField;
	@FXML JFXTextField nameField;
	@FXML JFXPasswordField passwordField;
	@FXML JFXPasswordField passwordConfirmationField;

	public void initialize(URL location, ResourceBundle resources) {
		if(Config.getInstance().isVerbose())
			Logger.getGlobal().info("Loading register controller.");
		super.initialize(location, resources);
	}

	@FXML
	private void submit() {
		Authenticator authenticator = MainController.getInstance().getAuthenticator();
		authenticator.register(emailField.getText(), passwordField.getText(), passwordConfirmationField.getText(), nameField.getText(),
				new FormCallback() {

			public void run() {
				if(getResponse().getStatus() == 200) {
					Config.getInstance().set("Token", authenticator.getToken());
					Config.getInstance().save();
					showNext(MainController.getInstance().getController("home"), true);
				}
				//TODO handle errors
			}

		});
	}

	@FXML
	private void clickBack() {
		back();
	}

}
