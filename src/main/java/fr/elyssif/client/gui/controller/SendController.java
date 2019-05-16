package fr.elyssif.client.gui.controller;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.NumberValidator;
import com.jfoenix.validation.RequiredFieldValidator;

import fr.elyssif.client.Config;
import fr.elyssif.client.callback.FailCallbackData;
import fr.elyssif.client.callback.FormCallbackData;
import fr.elyssif.client.gui.controller.SnackbarController.SnackbarMessageType;
import fr.elyssif.client.gui.model.File;
import fr.elyssif.client.gui.model.User;
import fr.elyssif.client.gui.repository.UserRepository;
import fr.elyssif.client.gui.validation.StringMaxLengthValidator;
import fr.elyssif.client.gui.validation.StringMinLengthValidator;
import fr.elyssif.client.gui.view.JFXNumberField;
import fr.elyssif.client.gui.view.LookupModal;
import fr.elyssif.client.gui.view.UserListFactory;
import fr.elyssif.client.security.Crypter;
import fr.elyssif.client.security.Hash;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;

/**
 * Controller for the "send file" view
 * @author Jérémy LAMBERT
 *
 */
public final class SendController extends EncryptionController implements Lockable, Validatable {

	@FXML private JFXTextField nameInput;
	@FXML private JFXNumberField priceInput;
	@FXML private JFXTextField fileInput;
	@FXML private JFXTextField recipientInput;

	@FXML private JFXButton browseButton;
	@FXML private JFXButton recipientButton;

	private java.io.File selectedFile;
	private User selectedUser;
	private File fileModel;

	public void initialize(URL location, ResourceBundle resources) {
		if(Config.getInstance().isVerbose())
			Logger.getGlobal().info("Loading send controller.");
		super.initialize(location, resources);
	}

	@Override
	public void show(boolean transition, Controller backController) {
		super.show(transition, backController);
		resetForm();
		resetValidation();
	}

	@FXML
	private void browseClicked() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle(getBundle().getString("browse-encrypt"));
		java.io.File file = fileChooser.showOpenDialog(getPane().getScene().getWindow());
		if(file != null && file.isFile()) {
			fileInput.setText(file.getName());
			selectedFile = file;
			fileInput.validate();
		}
	}

	@FXML
	private void recipientClicked() {
		LookupModal<User> modal = new LookupModal<User>(new UserRepository(), getBundle());
		modal.setTitle("recipient");
		modal.setHeader("lookup-recipient");
		modal.setListFactory(new UserListFactory());

		modal.showDialog((StackPane) MainController.getInstance().getPane(), model -> {
			if(model != null) {
				selectedUser = model;
				recipientInput.setText(selectedUser.getName().get());
				recipientInput.validate();
			}
		});
	}

	protected void onButtonClicked() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle(getBundle().getString("save-encrypt"));

		java.io.File dest = fileChooser.showSaveDialog(getPane().getScene().getWindow());
		if(dest != null) {
			if(dest.getAbsolutePath().equals(selectedFile.getAbsolutePath())) {
				SnackbarController.getInstance().message(getBundle().getString("invalid-file"), SnackbarMessageType.ERROR, 4000);
			} else {
				setDestinationFile(dest);
				setLocked(true);
				playAnimation();
			}
		}
	}

	@Override
	protected final void process(Runnable successCallback, Runnable failureCallback) {

		fileModel = new File();
		fileModel.setName(nameInput.getText());
		fileModel.setRecipientId(selectedUser.getId().get());

		if(priceInput.getText() != null && !priceInput.getText().isEmpty()) {
			fileModel.setPrice(priceInput.getValue());
		}

		Hash.sha256(selectedFile, digest -> {
			fileModel.setHash(Hash.toHex(digest));
			getFileRepository().store(fileModel, e -> encrypt(successCallback, failureCallback),
					data -> {
						handleValidationErrors(((FormCallbackData) data).getValidationErrors());
						revertAnimation();
						fileModel = null;
					}, errorData -> {
						SnackbarController.getInstance().message(((FailCallbackData) errorData).getFullMessage(), SnackbarMessageType.ERROR, 4000);
						revertAnimation();
						fileModel = null;
					});
		}, exception -> {
			Platform.runLater(() -> {
				SnackbarController.getInstance().message(exception.getMessage(), SnackbarMessageType.ERROR, 4000);
				resetForm();
				revertAnimation();
			});
		});
	}

	private void encrypt(Runnable successCallback, Runnable failureCallback) {

		Crypter crypter = new Crypter(selectedFile);
		crypter.encrypt(fileModel.getPublicKey().get(), getDestinationFile(), progress -> {
			Platform.runLater(() -> setProgress(progress));
		}, () -> {
			setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);

			Hash.sha256(getDestinationFile(), digest -> {
				fileModel.setHashCiphered(Hash.toHex(digest));
				getFileRepository().cipher(fileModel, data -> {
					SnackbarController.getInstance().message(getBundle().getString("encrypt-success").replace("\\n", "\n"), SnackbarMessageType.SUCCESS, 10000);
					successCallback.run();
					fileModel = null;
				}, data -> {
					SnackbarController.getInstance().message(String.join("\n", ((FormCallbackData) data).getValidationErrors().get("ciphered_hash")), SnackbarMessageType.ERROR, 4000);
					failureCallback.run();
					fileModel = null;
				}, errorData -> {
					if(errorData.getStatus() == 403) {
						SnackbarController.getInstance().message(getBundle().getString("forbidden"), SnackbarMessageType.ERROR, 4000);
					} else {
						SnackbarController.getInstance().message(((FailCallbackData) errorData).getFullMessage(), SnackbarMessageType.ERROR, 4000);
					}
					failureCallback.run();
					fileModel = null;
				});
			}, exception -> handleException(exception, failureCallback));
		}, exception -> handleException(exception, failureCallback));
	}

	private void handleException(Exception exception, Runnable failureCallback) {
		SnackbarController.getInstance().message(exception.getMessage(), SnackbarMessageType.ERROR, 4000);
		failureCallback.run();
		fileModel = null;
	}

	@Override
	public void setupValidators() {
		var requiredValidator = new RequiredFieldValidator(getBundle().getString("required"));
		var maxLengthValidator = new StringMaxLengthValidator(getBundle().getString("max-length").replace("%LENGTH%", "40"), 40);
		var minLengthValidator = new StringMinLengthValidator(getBundle().getString("min-length").replace("%LENGTH%", "3"), 3);
		var numberValidator = new NumberValidator("must be a number", priceInput.getConverter());

		nameInput.getValidators().add(requiredValidator);
		nameInput.getValidators().add(maxLengthValidator);
		nameInput.getValidators().add(minLengthValidator);
		priceInput.getValidators().add(numberValidator);
		fileInput.getValidators().add(requiredValidator);
		recipientInput.getValidators().add(requiredValidator);
		ValidationUtils.setValidationListener(nameInput);

		setupServerValidators();
	}

	@Override
	public void setupServerValidators() {
		nameInput.getValidators().add(createServerValidator("name"));
		fileInput.getValidators().add(createServerValidator("hash"));
		priceInput.getValidators().add(createServerValidator("price"));
		recipientInput.getValidators().add(createServerValidator("recipient_id"));
	}

	@Override
	public boolean validateAll() {
		boolean ok = nameInput.validate();
		ok = fileInput.validate() && ok;
		ok = (priceInput.getText() == null || priceInput.validate()) && ok;
		ok = recipientInput.validate() && ok;
		return ok;
	}

	@Override
	public void resetValidation() {
		nameInput.resetValidation();
		fileInput.resetValidation();
		priceInput.resetValidation();
		recipientInput.resetValidation();
	}

	@Override
	public void resetForm() {
		nameInput.setText(null);
		fileInput.setText(null);
		priceInput.setText(null);
		recipientInput.setText(null);
		selectedFile = null;
	}

}
