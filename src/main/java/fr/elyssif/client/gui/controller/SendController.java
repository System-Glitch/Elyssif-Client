package fr.elyssif.client.gui.controller;

import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.RequiredFieldValidator;

import fr.elyssif.client.Config;
import fr.elyssif.client.gui.controller.SnackbarController.SnackbarMessageType;
import fr.elyssif.client.gui.model.File;
import fr.elyssif.client.gui.model.ModelCallback;
import fr.elyssif.client.gui.model.User;
import fr.elyssif.client.gui.repository.FileRepository;
import fr.elyssif.client.gui.validation.ServerValidator;
import fr.elyssif.client.gui.validation.StringMaxLengthValidator;
import fr.elyssif.client.gui.validation.StringMinLengthValidator;
import fr.elyssif.client.http.FailCallback;
import fr.elyssif.client.http.FormCallback;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.stage.FileChooser;

/**
 * Controller for the "send file" view
 * @author Jérémy LAMBERT
 *
 */
public final class SendController extends FadeController implements Lockable, Validatable {

	@FXML private JFXTextField nameInput;
	@FXML private JFXTextField fileInput;
	@FXML private JFXTextField recipientInput;

	@FXML private JFXButton browseButton;
	@FXML private JFXButton recipientButton;
	@FXML private JFXButton encryptButton;

	private java.io.File selectedFile;
	private User selectedUser;
	private FileRepository fileRepository;

	private HashMap<String, ServerValidator> serverValidators;
	private SimpleBooleanProperty disableProperty;

	public void initialize(URL location, ResourceBundle resources) {
		if(Config.getInstance().isVerbose())
			Logger.getGlobal().info("Loading send controller.");
		super.initialize(location, resources);

		disableProperty = new SimpleBooleanProperty(false);
		serverValidators = new HashMap<String, ServerValidator>();
		bindControls();
		setupValidators();

		Platform.runLater(() -> fileRepository = new FileRepository());
	}

	protected java.io.File getSelectedFile() {
		return selectedFile;
	}

	@FXML
	public void browseClicked() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle(getBundle().getString("browse"));
		java.io.File file = fileChooser.showOpenDialog(getPane().getScene().getWindow());
		if(file != null) {
			fileInput.setText(file.getName());
			selectedFile = file;
			fileInput.validate();
		}
	}

	@FXML
	public void recipientClicked() {
		// TODO implement recipient searching

		selectedUser = MainController.getInstance().getAuthenticator().getUser();
		recipientInput.setText(selectedUser.getName().get());
		recipientInput.validate();
	}

	@FXML
	public void encryptClicked() {
		if(validateAll()) {
			setLocked(true);
			//
			File fileModel = new File();
			fileModel.setName(nameInput.getText());
			fileModel.setRecipientId(selectedUser.getId().get());
			fileModel.setHash("b94d27b9934d3e08a52e52d7da7dabfac484efe37a5380ee9088f7ace2efcde9"); // TODO hash
			fileRepository.store(fileModel, new ModelCallback<File>() {

				public void run() {
					Logger.getGlobal().info(fileModel.getId().asString().get());
					Logger.getGlobal().info(fileModel.getPublicKey().get());
					resetForm();
					setLocked(false);
				}

			}, new FormCallback() {

				public void run() {
					handleValidationErrors(getValidationErrors());
					setLocked(false);
				}

			}, new FailCallback() {

				public void run() {
					SnackbarController.getInstance().message(getFullMessage(), SnackbarMessageType.ERROR, 4000);
					setLocked(false);
				}

			});
		}
	}

	@Override
	public void setLocked(boolean locked) {
		disableProperty.set(locked);
	}

	@Override
	public void bindControls() {
		nameInput.disableProperty().bind(disableProperty);
		fileInput.disableProperty().bind(disableProperty);
		recipientInput.disableProperty().bind(disableProperty);
		browseButton.disableProperty().bind(disableProperty);
		recipientButton.disableProperty().bind(disableProperty);
		encryptButton.disableProperty().bind(disableProperty);
	}

	@Override
	public HashMap<String, ServerValidator> getServerValidators() {
		return serverValidators;
	}

	@Override
	public void setupValidators() {
		RequiredFieldValidator requiredValidator = new RequiredFieldValidator(getBundle().getString("required"));
		StringMaxLengthValidator maxLengthValidator = new StringMaxLengthValidator(getBundle().getString("max-length").replace("%LENGTH%", "255"), 255);
		StringMinLengthValidator minLengthValidator = new StringMinLengthValidator(getBundle().getString("min-length").replace("%LENGTH%", "3"), 3);

		nameInput.getValidators().add(requiredValidator);
		nameInput.getValidators().add(maxLengthValidator);
		nameInput.getValidators().add(minLengthValidator);
		fileInput.getValidators().add(requiredValidator);
		recipientInput.getValidators().add(requiredValidator);
		ValidationUtils.setValidationListener(nameInput);

		setupServerValidators();
	}

	@Override
	public void setupServerValidators() {
		nameInput.getValidators().add(createServerValidator("name"));
		fileInput.getValidators().add(createServerValidator("hash"));
		recipientInput.getValidators().add(createServerValidator("recipient_id"));
	}

	@Override
	public boolean validateAll() {
		boolean ok = nameInput.validate();
		ok = fileInput.validate() && ok;
		ok = recipientInput.validate() && ok;
		return ok;
	}

	@Override
	public void resetValidation() {
		nameInput.resetValidation();
		fileInput.resetValidation();
		recipientInput.resetValidation();
	}

	@Override
	public void resetForm() {
		nameInput.setText(null);
		fileInput.setText(null);
		recipientInput.setText(null);
		selectedFile = null;
	}

}
