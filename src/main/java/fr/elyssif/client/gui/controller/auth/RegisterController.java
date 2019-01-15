package fr.elyssif.client.gui.controller.auth;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.RequiredFieldValidator;

import fr.elyssif.client.Config;
import fr.elyssif.client.gui.controller.FadeController;
import fr.elyssif.client.gui.controller.Lockable;
import fr.elyssif.client.gui.controller.MainController;
import fr.elyssif.client.gui.controller.Validatable;
import fr.elyssif.client.gui.controller.ValidationUtils;
import fr.elyssif.client.gui.validation.StringMaxLengthValidator;
import fr.elyssif.client.gui.validation.StringMinLengthValidator;
import fr.elyssif.client.gui.validation.TextMatchValidator;
import fr.elyssif.client.http.Authenticator;
import fr.elyssif.client.http.FormCallback;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;

/**
 * Controller for the register view
 * @author Jérémy LAMBERT
 *
 */
public final class RegisterController extends FadeController implements Lockable, Validatable {

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
		setupValidators();
	}

	@FXML
	private void submit() {

		if(validateAll()) {
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
	}

	@FXML
	private void clickBack() {
		resetValidation();
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
		emailField.resetValidation();
		disableProperty.set(locked);
	}

	public void setupValidators() {
		RequiredFieldValidator requiredValidator = new RequiredFieldValidator(getBundle().getString("required"));
		StringMaxLengthValidator maxLengthValidator = new StringMaxLengthValidator(getBundle().getString("max-length").replace("%LENGTH%", "255"), 255);
		StringMinLengthValidator minLengthValidator = new StringMinLengthValidator(getBundle().getString("min-length").replace("%LENGTH%", "6"), 6);
		TextMatchValidator textMatchValidation = new TextMatchValidator(getBundle().getString("password-match"), passwordField);

		emailField.getValidators().add(requiredValidator);
		emailField.getValidators().add(maxLengthValidator);
		nameField.getValidators().add(requiredValidator);
		nameField.getValidators().add(maxLengthValidator);
		passwordField.getValidators().add(requiredValidator);
		passwordField.getValidators().add(maxLengthValidator);
		passwordField.getValidators().add(minLengthValidator);
		passwordConfirmationField.getValidators().add(requiredValidator);
		passwordConfirmationField.getValidators().add(maxLengthValidator);
		passwordConfirmationField.getValidators().add(minLengthValidator);
		passwordConfirmationField.getValidators().add(textMatchValidation);
		ValidationUtils.setValidationListener(emailField);
		ValidationUtils.setValidationListener(nameField);
		ValidationUtils.setValidationListener(passwordField);
		ValidationUtils.setValidationListener(passwordConfirmationField);
	}

	public boolean validateAll() {
		boolean ok = emailField.validate();
		ok = nameField.validate() && ok;
		ok = passwordField.validate() && ok;
		ok = passwordConfirmationField.validate() && ok;
		return ok;
	}

	public void resetValidation() {
		emailField.resetValidation();
		nameField.resetValidation();
		passwordField.resetValidation();
		passwordConfirmationField.resetValidation();
	}

}
