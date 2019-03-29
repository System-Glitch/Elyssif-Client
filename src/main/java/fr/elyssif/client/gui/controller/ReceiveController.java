package fr.elyssif.client.gui.controller;

import java.net.URL;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.RequiredFieldValidator;

import fr.elyssif.client.Config;
import fr.elyssif.client.gui.controller.SnackbarController.SnackbarMessageType;
import fr.elyssif.client.gui.model.File;
import fr.elyssif.client.gui.model.ModelCallback;
import fr.elyssif.client.gui.model.User;
import fr.elyssif.client.gui.view.ViewUtils;
import fr.elyssif.client.http.FailCallback;
import fr.elyssif.client.http.RestCallback;
import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.util.Duration;

/**
 * Controller for the "receive file" view
 * @author Jérémy LAMBERT
 *
 */
public final class ReceiveController extends EncryptionController implements Lockable, Validatable {

	@FXML private JFXTextField fileInput;
	@FXML private JFXButton browseButton;

	@FXML private VBox form;
	@FXML private VBox foundContainer;

	@FXML private Label fileNameLabel;
	@FXML private Label fromLabel;
	@FXML private JFXButton saveButton;
	@FXML private JFXButton cancelButton;

	private java.io.File selectedFile;
	private File fileModel;

	public void initialize(URL location, ResourceBundle resources) {
		if(Config.getInstance().isVerbose())
			Logger.getGlobal().info("Loading receive controller.");
		super.initialize(location, resources);
	}

	@Override
	public void show(boolean transition, Controller backController) {
		super.show(transition, backController);
		cancelButton.setDisable(true);
		saveButton.setDisable(true);
		resetForm();
		resetValidation();
		form.toFront();
		form.setDisable(false);
		form.setOpacity(1);
		foundContainer.setOpacity(0);
		fileModel = null;
	}

	@FXML
	private void browseClicked() {
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
	private void cancelClicked() {
		showForm();
	}

	@FXML
	private void saveClicked() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle(getBundle().getString("save-decrypt"));
		setDestinationFile(fileChooser.showSaveDialog(getPane().getScene().getWindow()));
		if(getDestinationFile() != null) {
			setLocked(true);
			showForm();
			playAnimation();
		}
	}

	protected void onButtonClicked() {
		form.setDisable(true);
		// TODO use real hash
		getFileRepository().fetch("03454af1793ba7be41f7789f9c1cbaebbdf7d967f8e45a0f747f24bc1c84108d", new ModelCallback<File>() {
			public void run() {
				fileModel = getModel();
				showFileFound();
			}
		}, new FailCallback() {
			public void run() {
				if(getStatus() == 404) {
					SnackbarController.getInstance().message(getBundle().getString("file-not-found"), SnackbarMessageType.ERROR, 4000);
				} else {
					SnackbarController.getInstance().message(getBundle().getString(getMessage()), SnackbarMessageType.ERROR, 4000);					
				}
				form.setDisable(false);
			}
		});
	}

	private void updateFileFound() {
		fileNameLabel.setText(fileModel.getName().get());

		User sender = fileModel.getSender().get();
		fromLabel.setText(sender.getName().get() + "\n" + sender.getEmail().get());
	}

	private void showFileFound() {
		updateFileFound();
		FadeTransition ft = ViewUtils.createFadeOutTransition(form, Duration.millis(500));
		FadeTransition ft2 = ViewUtils.createFadeInTransition(foundContainer, Duration.millis(500));

		ft.setOnFinished(e -> {
			foundContainer.toFront();
			cancelButton.setDisable(false);
			saveButton.setDisable(false);
			ft2.play();
		});

		ft.play();
	}

	private void showForm() {
		if(!saveButton.isDisable()) {
			cancelButton.setDisable(true);
			saveButton.setDisable(true);
			resetForm();

			FadeTransition ft = ViewUtils.createFadeOutTransition(foundContainer, Duration.millis(500));
			FadeTransition ft2 = ViewUtils.createFadeInTransition(form, Duration.millis(500));

			ft.setOnFinished(e -> {
				form.toFront();
				form.setDisable(false);
				ft2.play();
			});

			ft.play();
		}
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

		fileModel.setHash("b94d27b9934d3e08a52e52d7da7dabfac484efe37a5380ee9088f7ace2efcde8"); // TODO real hash
		fileModel.setHashCiphered("03454af1793ba7be41f7789f9c1cbaebbdf7d967f8e45a0f747f24bc1c84108d"); // TODO real ciphered hash
		getFileRepository().check(fileModel, new RestCallback() {
			public void run() {
				SnackbarController.getInstance().message(getBundle().getString("decrypt-success").replace("\\n", "\n"), SnackbarMessageType.SUCCESS, 10000);
				successCallback.run();
				fileModel = null;
			}
		}, new FailCallback() {
			public void run() {
				if(getStatus() == 404) {
					openFailDialog(successCallback, failureCallback);
				} else {
					SnackbarController.getInstance().message(getStatus() + ": " + getBundle().getString("server-error").replace("\\n", "\n"), SnackbarMessageType.ERROR, 4000);
					failureCallback.run();
					fileModel = null;
				}
			}
		});
	}

	private void openFailDialog(Runnable successCallback, Runnable failureCallback) {
		final JFXDialog dialog = new JFXDialog();
		dialog.setDialogContainer((StackPane) MainController.getInstance().getPane());

		JFXDialogLayout content = new JFXDialogLayout();
		Label header = new Label(getBundle().getString("file-check-fail-header"), new ImageView("view/img/warning.png"));
		header.getStyleClass().add("text-white");
		content.setHeading(header);
		Label body = new Label(getBundle().getString("file-check-fail").replace("\\n", "\n"));
		body.getStyleClass().add("text-md");
		content.setBody(body);
		content.getStyleClass().add("dialog-warning");

		JFXButton acceptButton = new JFXButton(getBundle().getString("yes"));
		acceptButton.getStyleClass().add("green-A700");
		acceptButton.setOnAction(e -> {
			fileModel = null;
			dialog.close();
			successCallback.run();
		});
		ImageView image = new ImageView("view/img/save.png");
		image.setFitWidth(24);
		image.setFitHeight(24);
		acceptButton.setGraphic(image);

		JFXButton cancelButton = new JFXButton(getBundle().getString("cancel"));
		cancelButton.setMaxHeight(Double.MAX_VALUE);
		cancelButton.setOnAction(e -> {
			fileModel = null;
			dialog.close();
			failureCallback.run();
		});

		content.setActions(cancelButton, acceptButton);

		dialog.setContent(content);
		dialog.setTransitionType(JFXDialog.DialogTransition.CENTER);
		dialog.setOverlayClose(false);
		dialog.show();
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
	}

}
