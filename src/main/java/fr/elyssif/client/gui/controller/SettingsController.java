package fr.elyssif.client.gui.controller;

import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.RequiredFieldValidator;

import fr.elyssif.client.Config;
import fr.elyssif.client.callback.FailCallbackData;
import fr.elyssif.client.callback.FormCallbackData;
import fr.elyssif.client.gui.controller.SnackbarController.SnackbarMessageType;
import fr.elyssif.client.gui.model.User;
import fr.elyssif.client.gui.repository.UserRepository;
import fr.elyssif.client.gui.validation.ServerValidator;
import fr.elyssif.client.gui.validation.StringMaxLengthValidator;
import fr.elyssif.client.gui.validation.StringMinLengthValidator;
import fr.elyssif.client.gui.validation.TextMatchValidator;
import fr.elyssif.client.gui.view.Language;
import fr.elyssif.client.gui.view.ViewUtils;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.layout.StackPane;

/**
 * Controller for the settings view
 * @author Jérémy LAMBERT
 *
 */
public final class SettingsController extends FadeController implements Lockable, Validatable {

	@FXML private JFXTextField emailField;
	@FXML private JFXTextField nameField;
	@FXML private JFXTextField addressField;

	@FXML private JFXPasswordField passwordField;
	@FXML private JFXPasswordField newPasswordField;
	@FXML private JFXPasswordField passwordConfirmationField;

	@FXML private JFXComboBox<Language> languageInput;

	@FXML private JFXButton submitButton;
	@FXML private JFXButton submitPasswordButton;

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

			final User user = MainController.getInstance().getAuthenticator().getUser();

			final String previousAddress = user.getAddress().get();
			final boolean hasAddress = addressField.getText() != null && !addressField.getText().isEmpty();

			if(previousAddress != null && !previousAddress.isEmpty() && !hasAddress) {
				ViewUtils.buildConfirmDialog((StackPane) getPane().getParent(), getBundle(), "address-change-notice", false, e -> {
					submitUpdate(user, hasAddress);
				}, true).show();
			} else {
				submitUpdate(user, hasAddress);
			}
		}
	}

	private void submitUpdate(User user, boolean hasAddress) {
		setLocked(true);
		final String previousEmail = user.getEmail().get();
		final String previousName = user.getName().get();
		final String previousAddress = user.getAddress().get();

		user.setEmail(emailField.getText());
		user.setName(nameField.getText());
		user.setAddress(addressField.getText());

		userRepository.update(user, data -> {
			setLocked(false);
			if(!hasAddress) {
				user.setAddress(null);
			}
			SnackbarController.getInstance().message(getBundle().getString("change-success"), SnackbarMessageType.SUCCESS, 4000);
		}, errorData -> {
			setLocked(false);
			user.setEmail(previousEmail);
			user.setName(previousName);
			user.setAddress(previousAddress);
			handleValidationErrors(((FormCallbackData) errorData).getValidationErrors());
		}, "email", "name", "address");
	}

	@FXML
	private void submitPassword() {
		if(validatePassword()) {
			setLocked(true);

			final User user = MainController.getInstance().getAuthenticator().getUser();

			userRepository.updatePassword(passwordField.getText(), newPasswordField.getText(), passwordConfirmationField.getText(), data -> {
				user.setUpdatedAt(new Date());
				resetForm();
				setLocked(false);
				SnackbarController.getInstance().message(getBundle().getString("password-change-success"), SnackbarMessageType.SUCCESS, 4000);
			}, errorData -> {
				setLocked(false);
				handleValidationErrors(((FormCallbackData) errorData).getValidationErrors());
			}, failData -> {
				setLocked(false);
				SnackbarController.getInstance().message(getBundle().getString(((FailCallbackData) failData).getFullMessage()), SnackbarMessageType.ERROR);
			});
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
		StringMinLengthValidator minLengthValidator = new StringMinLengthValidator(getBundle().getString("min-length").replace("%LENGTH%", "6"), 6);
		TextMatchValidator textMatchValidation = new TextMatchValidator(getBundle().getString("password-match"), newPasswordField);

		emailField.getValidators().add(requiredValidator);
		emailField.getValidators().add(maxLengthValidator);
		nameField.getValidators().add(requiredValidator);
		nameField.getValidators().add(maxLengthValidator);
		addressField.getValidators().add(maxLengthValidator);

		passwordField.getValidators().add(requiredValidator);
		newPasswordField.getValidators().add(requiredValidator);
		newPasswordField.getValidators().add(maxLengthValidator);
		newPasswordField.getValidators().add(minLengthValidator);
		passwordConfirmationField.getValidators().add(requiredValidator);
		passwordConfirmationField.getValidators().add(maxLengthValidator);
		passwordConfirmationField.getValidators().add(minLengthValidator);
		passwordConfirmationField.getValidators().add(textMatchValidation);

		ValidationUtils.setValidationListener(emailField);
		ValidationUtils.setValidationListener(nameField);
		ValidationUtils.setValidationListener(addressField);
		ValidationUtils.setValidationListener(passwordField);
		ValidationUtils.setValidationListener(newPasswordField);
		ValidationUtils.setValidationListener(passwordConfirmationField);

		setupServerValidators();
	}

	@Override
	public void setupServerValidators() {
		emailField.getValidators().add(createServerValidator("email"));
		nameField.getValidators().add(createServerValidator("name"));

		// Bitcoin address is only validated server-side so validaton
		// always match our node Bitcoin version, in case of new BIP implementations.
		// Removes the need for a client update in such a scenario.
		addressField.getValidators().add(createServerValidator("address"));
		passwordField.getValidators().add(createServerValidator("old_password"));
		newPasswordField.getValidators().add(createServerValidator("password"));
		passwordConfirmationField.getValidators().add(createServerValidator("password_confirmation"));
	}

	@Override
	public boolean validateAll() {
		boolean ok = emailField.validate();
		ok = nameField.validate() && ok;
		ok = addressField.validate() && ok;
		return ok;
	}

	private boolean validatePassword() {
		boolean ok = passwordField.validate();
		ok = newPasswordField.validate() && ok;
		ok = passwordConfirmationField.validate() && ok;
		return ok;
	}

	@Override
	public void resetValidation() {
		emailField.resetValidation();
		nameField.resetValidation();
		addressField.resetValidation();
		passwordField.resetValidation();
		newPasswordField.resetValidation();
		passwordConfirmationField.resetValidation();
	}

	@Override
	public void resetForm() {
		final User user = MainController.getInstance().getAuthenticator().getUser();
		emailField.setText(user.getEmail().get());
		nameField.setText(user.getName().get());
		addressField.setText(user.getAddress().get());

		passwordField.setText(null);
		newPasswordField.setText(null);
		passwordConfirmationField.setText(null);
	}

	@Override
	public void bindControls() {
		emailField.disableProperty().bind(disableProperty);
		nameField.disableProperty().bind(disableProperty);
		addressField.disableProperty().bind(disableProperty);
		passwordField.disableProperty().bind(disableProperty);
		newPasswordField.disableProperty().bind(disableProperty);
		passwordConfirmationField.disableProperty().bind(disableProperty);
		submitButton.disableProperty().bind(disableProperty);
		submitPasswordButton.disableProperty().bind(disableProperty);
		languageInput.disableProperty().bind(disableProperty);
	}

	@Override
	public void setLocked(boolean locked) {
		disableProperty.set(locked);
	}

}
