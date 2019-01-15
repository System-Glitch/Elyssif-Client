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
 * Controller for the login view
 * @author Jérémy LAMBERT
 *
 */
public final class LoginController extends FadeController {

	@FXML JFXTextField emailField;
	@FXML JFXPasswordField passwordField;
	
	public void initialize(URL location, ResourceBundle resources) {
		if(Config.getInstance().isVerbose())
			Logger.getGlobal().info("Loading login controller.");
		super.initialize(location, resources);
	}
	
	@FXML
	private void submit() {
		Authenticator authenticator = MainController.getInstance().getAuthenticator();
		authenticator.login(emailField.getText(), passwordField.getText(), new FormCallback() {

			public void run() {
				if(getResponse().getStatus() == 200) {
					Config.getInstance().set("Token", authenticator.getToken());
					Config.getInstance().save();
					showNext(MainController.getInstance().getController("home"), true);
				}
				//TODO handle auth fail
			}

		});
	}
	
	@FXML
	private void clickBack() {
		back();
	}

}
