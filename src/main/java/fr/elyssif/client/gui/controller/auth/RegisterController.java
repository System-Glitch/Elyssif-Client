package fr.elyssif.client.gui.controller.auth;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;

import fr.elyssif.client.Config;
import fr.elyssif.client.gui.controller.FadeController;
import fr.elyssif.client.gui.controller.Lockable;
import fr.elyssif.client.gui.controller.MainController;
import fr.elyssif.client.http.Authenticator;
import fr.elyssif.client.http.FormCallback;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;

/**
 * Controller for the register view
 * @author Jérémy LAMBERT
 *
 */
public final class RegisterController extends FadeController implements Lockable {

	@FXML JFXTextField emailField;
	@FXML JFXTextField nameField;
	@FXML JFXPasswordField passwordField;
	@FXML JFXPasswordField passwordConfirmationField;

	@FXML JFXButton submitButton;
	@FXML JFXButton backButton;

	private SimpleBooleanProperty disableProperty;

	public void initialize(URL location, ResourceBundle resources) {
		if(Config.getInstance().isVerbose())
			Logger.getGlobal().info("Loading register controller.");
		super.initialize(location, resources);

		disableProperty = new SimpleBooleanProperty(false);
		bindControls();
	}

	@FXML
	private void submit() {
		setLocked(true);
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
				setLocked(false);
			}

		});
	}

	@FXML
	private void clickBack() {
		back();
	}

	public void bindControls() {
		emailField.disableProperty().bind(disableProperty);
		nameField.disableProperty().bind(disableProperty);
		passwordField.disableProperty().bind(disableProperty);
		passwordConfirmationField.disableProperty().bind(disableProperty);
		submitButton.disableProperty().bind(disableProperty);
		backButton.disableProperty().bind(disableProperty);
	}

	public void setLocked(boolean locked) {
		disableProperty.set(locked);
	}

}
