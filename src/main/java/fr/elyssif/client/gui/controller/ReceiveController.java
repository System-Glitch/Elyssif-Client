package fr.elyssif.client.gui.controller;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import com.jfoenix.controls.JFXSpinner;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.RequiredFieldValidator;

import fr.elyssif.client.Config;
import fr.elyssif.client.callback.FailCallbackData;
import fr.elyssif.client.callback.ModelCallbackData;
import fr.elyssif.client.gui.controller.SnackbarController.SnackbarMessageType;
import fr.elyssif.client.gui.model.File;
import fr.elyssif.client.gui.model.User;
import fr.elyssif.client.gui.view.ViewUtils;
import fr.elyssif.client.security.Crypter;
import fr.elyssif.client.security.Hash;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
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

	@FXML private JFXSpinner hashSpinner;

	private java.io.File selectedFile;
	private String hashCiphered;

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
		reset();
		resetForm();
		resetValidation();
		form.toFront();
		form.setDisable(false);
		form.setOpacity(1);
		foundContainer.setOpacity(0);
		fileModel = null;

		browseButton.requestFocus();
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
		reset();
		showForm();
	}

	@FXML
	private void saveClicked() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle(getBundle().getString("save-decrypt"));
		java.io.File dest = fileChooser.showSaveDialog(getPane().getScene().getWindow());
		if(dest != null) {
			if(dest.getAbsolutePath().equals(selectedFile.getAbsolutePath())) {
				SnackbarController.getInstance().message(getBundle().getString("invalid-file"), SnackbarMessageType.ERROR, 4000);
			} else {
				setDestinationFile(dest);
				setLocked(true);
				showForm();
				playAnimation();
			}
		}
	}


	@SuppressWarnings("unchecked")
	protected void onButtonClicked() {
		form.setDisable(true);
		showHashSpinner();

		Hash.sha256(selectedFile, digest -> {
			hashCiphered = Hash.toHex(digest);
			getFileRepository().fetch(hashCiphered, data -> {
				fileModel = ((ModelCallbackData<File>) data).getModel();
				hideHashSpinner();
				showFileFound();
			}, errorData -> {
				if(errorData.getStatus() == 404) {
					SnackbarController.getInstance().message(getBundle().getString("file-not-found"), SnackbarMessageType.ERROR, 4000);
				} else {
					SnackbarController.getInstance().message(getBundle().getString(((FailCallbackData) errorData).getMessage()), SnackbarMessageType.ERROR, 4000);
				}
				form.setDisable(false);
				hideHashSpinner();
			});
		}, exception -> {
			Platform.runLater(() -> {
				SnackbarController.getInstance().message(exception.getMessage().replace("server-error", getBundle().getString("server-error")), SnackbarMessageType.ERROR, 4000);
				hideHashSpinner();
				resetForm();
				revertAnimation();
			});
		});
	}

	private void showHashSpinner() {
		hashSpinner.setVisible(true);
		FadeTransition ft = ViewUtils.createFadeInTransition(hashSpinner);
		ft.play();
	}

	private void hideHashSpinner() {
		FadeTransition ft = ViewUtils.createFadeOutTransition(hashSpinner);
		ft.setOnFinished(e -> {
			hashSpinner.setVisible(false);
		});
		ft.play();
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

		Crypter crypter = new Crypter(selectedFile);
		crypter.decrypt(fileModel.getPrivateKey().get(), getDestinationFile(), progress -> {
			Platform.runLater(() -> setProgress(progress));
		}, () -> {
			Hash.sha256(getDestinationFile(), digest -> {
				fileModel.setHash(Hash.toHex(digest));
				fileModel.setHashCiphered(hashCiphered);
				getFileRepository().check(fileModel, data -> {
					SnackbarController.getInstance().message(getBundle().getString("decrypt-success").replace("\\n", "\n"), SnackbarMessageType.SUCCESS, 10000);
					successCallback.run();
					reset();
				}, errorData -> {
					if(errorData.getStatus() == 404) {
						openFailDialog(successCallback, failureCallback);
					} else {
						SnackbarController.getInstance().message(errorData.getStatus() + ": " + getBundle().getString("server-error").replace("\\n", "\n"), SnackbarMessageType.ERROR, 4000);
						failureCallback.run();
						reset();
					}
				});
			}, exception -> handleException(exception, failureCallback));
		}, exception -> handleException(exception, failureCallback));
	}

	private void handleException(Exception exception, Runnable failureCallback) {
		SnackbarController.getInstance().message(exception.getMessage(), SnackbarMessageType.ERROR, 4000);
		failureCallback.run();
		reset();
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
	}

	public void reset() {
		fileModel = null;
		selectedFile = null;
		hashCiphered = null;
	}

}
