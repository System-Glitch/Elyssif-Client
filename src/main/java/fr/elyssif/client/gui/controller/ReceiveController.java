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
import fr.elyssif.client.http.FailCallback;
import fr.elyssif.client.http.JsonCallback;
import javafx.fxml.FXML;
import javafx.stage.FileChooser;

/**
 * Controller for the "receive file" view
 * @author Jérémy LAMBERT
 *
 */
public final class ReceiveController extends EncryptionController implements Lockable, Validatable {

	@FXML private JFXTextField fileInput;
	@FXML private JFXButton browseButton;

	private java.io.File selectedFile;
	private String privateKey;

	public void initialize(URL location, ResourceBundle resources) {
		if(Config.getInstance().isVerbose())
			Logger.getGlobal().info("Loading receive controller.");
		super.initialize(location, resources);
	}

	@FXML
	public void browseClicked() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle(getBundle().getString("browse-decrypt"));
		java.io.File file = fileChooser.showOpenDialog(getPane().getScene().getWindow());
		if(file != null && file.isFile()) {
			fileInput.setText(file.getName());
			selectedFile = file;
			fileInput.validate();
		}
	}

	@FXML
	public void onButtonClicked() {
		setLocked(true);

		getFileRepository().fetch("03454af1793ba7be41f7789f9c1cbaebbdf7d967f8e45a0f747f24bc1c84108d", new JsonCallback() {
			public void run() {
				privateKey = getElement().getAsString();

				// TODO show popup
				FileChooser fileChooser = new FileChooser();
				fileChooser.setTitle(getBundle().getString("save-decrypt"));
				setDestinationFile(fileChooser.showSaveDialog(getPane().getScene().getWindow()));
				if(getDestinationFile() != null) {
					playAnimation();
				}
			}
		}, new FailCallback() {
			public void run() {
				if(getStatus() == 404) {
					SnackbarController.getInstance().message(getBundle().getString("file-not-found"), SnackbarMessageType.ERROR, 4000);
				} else {
					SnackbarController.getInstance().message(getBundle().getString(getMessage()), SnackbarMessageType.ERROR, 4000);					
				}
				setLocked(false);
			}
		});
	}

	@Override
	protected final void process(Runnable successCallback, Runnable failureCallback) {
		// TODO decrypt
		Random random = new Random();
		while(getProgress() < 1) {
			setProgress(getProgress() + random.nextDouble() / 100);
			try {
				Thread.sleep(25);
			} catch (InterruptedException ie) {
				ie.printStackTrace();
				failureCallback.run();
			}
		}

		SnackbarController.getInstance().message(getBundle().getString("decrypt-success").replace("\\n", "\n"), SnackbarMessageType.SUCCESS, 10000);
		successCallback.run();
	}

	@Override
	public void setupValidators() {
		RequiredFieldValidator requiredValidator = new RequiredFieldValidator(getBundle().getString("required"));

		fileInput.getValidators().add(requiredValidator);

		setupServerValidators();
	}

	@Override
	public void setupServerValidators() {
		fileInput.getValidators().add(createServerValidator("ciphered_hash"));
	}

	@Override
	public boolean validateAll() {
		return fileInput.validate();
	}

	@Override
	public void resetValidation() {
		fileInput.resetValidation();
	}

	@Override
	public void resetForm() {
		fileInput.setText(null);
		selectedFile = null;
		privateKey = null;
	}

}
