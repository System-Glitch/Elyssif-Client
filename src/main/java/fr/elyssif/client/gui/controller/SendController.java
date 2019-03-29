package fr.elyssif.client.gui.controller;

import java.net.URL;
import java.util.Random;
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
import fr.elyssif.client.gui.repository.UserRepository;
import fr.elyssif.client.gui.validation.StringMaxLengthValidator;
import fr.elyssif.client.gui.validation.StringMinLengthValidator;
import fr.elyssif.client.gui.view.LookupModal;
import fr.elyssif.client.gui.view.UserListFactory;
import fr.elyssif.client.http.FailCallback;
import fr.elyssif.client.http.FormCallback;
import fr.elyssif.client.http.RestCallback;
import javafx.fxml.FXML;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;

/**
 * Controller for the "send file" view
 * @author Jérémy LAMBERT
 *
 */
public final class SendController extends EncryptionController implements Lockable, Validatable {

	@FXML private JFXTextField nameInput;
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
		LookupModal<User> modal = new LookupModal<User>(new UserRepository());
		modal.setTitle("recipient");
		modal.setHeader("lookup-recipient");
		modal.setTableFactory(new UserListFactory());

		modal.showDialog((StackPane) MainController.getInstance().getPane(), new ModelCallback<User>() {

			@Override
			public void run() {
				User user = getModel();
				if(user != null) {
					selectedUser = user;
					recipientInput.setText(selectedUser.getName().get());
					recipientInput.validate();
				}
			}

		});
	}


	protected void onButtonClicked() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle(getBundle().getString("save-encrypt"));
		setDestinationFile(fileChooser.showSaveDialog(getPane().getScene().getWindow()));
		if(getDestinationFile() != null) {
			setLocked(true);
			fileModel = new File();
			fileModel.setName(nameInput.getText());
			fileModel.setRecipientId(selectedUser.getId().get());
			fileModel.setHash("b94d27b9934d3e08a52e52d7da7dabfac484efe37a5380ee9088f7ace2efcde9"); // TODO hash
			getFileRepository().store(fileModel, new ModelCallback<File>() {

				public void run() {
					Logger.getGlobal().info(fileModel.getId().asString().get());
					Logger.getGlobal().info(fileModel.getPublicKey().get());

					playAnimation();
				}

			}, new FormCallback() {

				public void run() {
					handleValidationErrors(getValidationErrors());
					setLocked(false);
					fileModel = null;
				}

			}, new FailCallback() {

				public void run() {
					SnackbarController.getInstance().message(getFullMessage(), SnackbarMessageType.ERROR, 4000);
					setLocked(false);
					fileModel = null;
				}

			});
		}
	}

	@Override
	protected final void process(Runnable successCallback, Runnable failureCallback) {
		// TODO encrypt
		Random random = new Random();
		while(getProgress() < 1) {
			setProgress(getProgress() + random.nextDouble() / 100);
			try {
				Thread.sleep(25);
			} catch (InterruptedException ie) {
				ie.printStackTrace();
			}
		}

		fileModel.setHashCiphered("03454af1793ba7be41f7789f9c1cbaebbdf7d967f8e45a0f747f24bc1c84108d"); // TODO hash ciphered
		getFileRepository().cipher(fileModel, new RestCallback() {

			public void run() {
				SnackbarController.getInstance().message(getBundle().getString("encrypt-success").replace("\\n", "\n"), SnackbarMessageType.SUCCESS, 10000);
				successCallback.run();
				fileModel = null;
			}

		}, new FormCallback() {
			public void run() {
				SnackbarController.getInstance().message(String.join("\n", getValidationErrors().get("ciphered_hash")), SnackbarMessageType.ERROR, 4000);
				failureCallback.run();
				fileModel = null;
			}
		}, new FailCallback() {
			public void run() {
				SnackbarController.getInstance().message(getFullMessage(), SnackbarMessageType.ERROR, 4000);
				failureCallback.run();
				fileModel = null;
			}
		});
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
