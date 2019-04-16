package fr.elyssif.client.gui.controller;

import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.RequiredFieldValidator;

import fr.elyssif.client.Config;
import fr.elyssif.client.callback.FormCallbackData;
import fr.elyssif.client.gui.controller.SnackbarController.SnackbarMessageType;
import fr.elyssif.client.gui.model.User;
import fr.elyssif.client.gui.repository.UserRepository;
import fr.elyssif.client.gui.validation.ServerValidator;
import fr.elyssif.client.gui.validation.StringMaxLengthValidator;
import fr.elyssif.client.gui.view.Language;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;

/**
 * Controller for the settings view
 * @author Jérémy LAMBERT
 *
 */
public final class SettingsController extends FadeController implements Lockable, Validatable {

	@FXML private JFXTextField emailField;
	@FXML private JFXTextField nameField;

	@FXML private JFXComboBox<Language> languageInput;

	@FXML private JFXButton submitButton;

	private HashMap<String, ServerValidator> serverValidators;
	private SimpleBooleanProperty disableProperty;
	private UserRepository userRepository;

	public void initialize(URL location, ResourceBundle resources) {
		if(Config.getInstance().isVerbose())
			Logger.getGlobal().info("Loading settings controller.");
		super.initialize(location, resources);

		disableProperty = new SimpleBooleanProperty(false);
		serverValidators = new HashMap<String, ServerValidator>();
		bindControls();
		setupValidators();
		initLanguageInput();
		Platform.runLater(() -> userRepository = new UserRepository());
	}

	/**
	 * {@inheritDoc}
	 */
	protected void show(boolean transition, Controller backController) {
		super.show(transition, backController);
		resetForm();
		resetValidation();
	}

	@FXML
	private void submit() {
		if(validateAll()) {
			setLocked(true);

			final User user = MainController.getInstance().getAuthenticator().getUser();
			final String previousEmail = user.getEmail().get();
			final String previousName = user.getName().get();

			user.setEmail(emailField.getText());
			user.setName(nameField.getText());

			userRepository.update(user, data -> {
				setLocked(false);
				SnackbarController.getInstance().message(getBundle().getString("change-success"), SnackbarMessageType.SUCCESS, 4000);
			}, errorData -> {
				setLocked(false);
				user.setEmail(previousEmail);
				user.setName(previousName);
				handleValidationErrors(((FormCallbackData) errorData).getValidationErrors());
			}, "email", "name");
		}
	}

	@FXML
	private void onLanguageChange() {
		Config config = Config.getInstance();
		String shortCode = languageInput.getSelectionModel().getSelectedItem().getShortCode();

		if(!shortCode.equals(config.get("Locale"))) {
			config.set("Locale", shortCode);
			config.save();
			SnackbarController.getInstance().message(getBundle().getString("lang-change"), SnackbarMessageType.SUCCESS, 4000);			
		}
	}

	private void initLanguageInput() {
		languageInput.setItems(FXCollections.observableArrayList(Language.values()));

		Language lang = Language.fromShortCode(Config.getInstance().get("Locale"));
		languageInput.getSelectionModel().select(lang != null ? lang : Language.ENGLISH);
	}

	@Override
	public HashMap<String, ServerValidator> getServerValidators() {
		return serverValidators;
	}

	@Override
	public void setupValidators() {
		RequiredFieldValidator requiredValidator = new RequiredFieldValidator(getBundle().getString("required"));
		StringMaxLengthValidator maxLengthValidator = new StringMaxLengthValidator(getBundle().getString("max-length").replace("%LENGTH%", "255"), 255);

		emailField.getValidators().add(requiredValidator);
		emailField.getValidators().add(maxLengthValidator);
		nameField.getValidators().add(requiredValidator);
		nameField.getValidators().add(maxLengthValidator);
		ValidationUtils.setValidationListener(emailField);
		ValidationUtils.setValidationListener(nameField);

		setupServerValidators();
	}

	@Override
	public void setupServerValidators() {
		emailField.getValidators().add(createServerValidator("email"));
		nameField.getValidators().add(createServerValidator("name"));
	}

	@Override
	public boolean validateAll() {
		boolean ok = emailField.validate();
		ok = nameField.validate() && ok;
		return ok;
	}

	@Override
	public void resetValidation() {
		emailField.resetValidation();
		nameField.resetValidation();
	}

	@Override
	public void resetForm() {
		final User user = MainController.getInstance().getAuthenticator().getUser();
		emailField.setText(user.getEmail().get());
		nameField.setText(user.getName().get());
	}

	@Override
	public void bindControls() {
		emailField.disableProperty().bind(disableProperty);
		nameField.disableProperty().bind(disableProperty);
		submitButton.disableProperty().bind(disableProperty);
		languageInput.disableProperty().bind(disableProperty);
	}

	@Override
	public void setLocked(boolean locked) {
		disableProperty.set(locked);
	}

}
