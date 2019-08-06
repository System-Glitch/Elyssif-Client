/*
 * Elyssif-Client
 * Copyright (C) 2019 Jérémy LAMBERT (System-Glitch)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
 package fr.elyssif.client.gui.controller.auth;

import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.RequiredFieldValidator;

import fr.elyssif.client.Config;
import fr.elyssif.client.callback.FormCallbackData;
import fr.elyssif.client.gui.controller.FadeController;
import fr.elyssif.client.gui.controller.Lockable;
import fr.elyssif.client.gui.controller.MainController;
import fr.elyssif.client.gui.controller.SnackbarController;
import fr.elyssif.client.gui.controller.SnackbarController.SnackbarMessageType;
import fr.elyssif.client.gui.controller.Validatable;
import fr.elyssif.client.gui.controller.ValidationUtils;
import fr.elyssif.client.gui.validation.ServerValidator;
import fr.elyssif.client.gui.validation.StringMaxLengthValidator;
import fr.elyssif.client.http.Authenticator;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;

/**
 * Controller for the login view
 * @author Jérémy LAMBERT
 *
 */
public final class LoginController extends FadeController implements Lockable, Validatable {

	@FXML private JFXTextField emailField;
	@FXML private JFXPasswordField passwordField;

	@FXML private JFXButton submitButton;
	@FXML private JFXButton backButton;

	private HashMap<String, ServerValidator> serverValidators;
	private SimpleBooleanProperty disableProperty;

	public void initialize(URL location, ResourceBundle resources) {
		if(Config.getInstance().isVerbose())
			Logger.getGlobal().info("Loading login controller.");
		super.initialize(location, resources);

		disableProperty = new SimpleBooleanProperty(false);
		serverValidators = new HashMap<String, ServerValidator>();
		bindControls();
		setupValidators();
	}

	@FXML
	private void submit() {

		if(validateAll()) {
			setLocked(true);
			Authenticator authenticator = MainController.getInstance().getAuthenticator();
			authenticator.login(emailField.getText(), passwordField.getText(), data -> {

				int status = data.getStatus();
				if(status == 200) {
					Config.getInstance().set("Token", authenticator.getToken());
					Config.getInstance().save();
					showNext(MainController.getInstance().getController("app"), true);
				} else if(status == 422) { //Validation errors
					handleValidationErrors(((FormCallbackData) data).getValidationErrors());
				} else if(status == -1)
					SnackbarController.getInstance().message(getBundle().getString("error") + data.getResponse().getRawBody(), SnackbarMessageType.ERROR, 4000);
				setLocked(false);

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
		passwordField.disableProperty().bind(disableProperty);
		submitButton.disableProperty().bind(disableProperty);
		backButton.disableProperty().bind(disableProperty);

		//Disable cancel and default if pane is not visible
		backButton.cancelButtonProperty().bind(getPane().disabledProperty().not());
		submitButton.defaultButtonProperty().bind(getPane().disabledProperty().not());
	}

	@Override
	public void setLocked(boolean locked) {
		disableProperty.set(locked);
	}

	@Override
	public void setupValidators() {
		RequiredFieldValidator requiredValidator = new RequiredFieldValidator(getBundle().getString("required"));
		StringMaxLengthValidator maxLengthValidator = new StringMaxLengthValidator(getBundle().getString("max-length").replace("%LENGTH%", "255"), 255);

		emailField.getValidators().add(requiredValidator);
		emailField.getValidators().add(maxLengthValidator);
		passwordField.getValidators().add(requiredValidator);
		passwordField.getValidators().add(maxLengthValidator);
		ValidationUtils.setValidationListener(emailField);
		ValidationUtils.setValidationListener(passwordField);

		setupServerValidators();
	}

	@Override
	public void setupServerValidators() {
		emailField.getValidators().add(createServerValidator("email"));
		passwordField.getValidators().add(createServerValidator("password"));
	}

	@Override
	public boolean validateAll() {
		boolean ok = emailField.validate();
		ok = passwordField.validate() && ok;
		return ok;
	}

	@Override
	public void resetValidation() {
		emailField.resetValidation();
		passwordField.resetValidation();
	}

	@Override
	public HashMap<String, ServerValidator> getServerValidators() {
		return serverValidators;
	}

	@Override
	public void resetForm() {
		emailField.setText(null);
		passwordField.setText(null);
	}

}
