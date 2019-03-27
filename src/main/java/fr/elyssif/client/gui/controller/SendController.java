package fr.elyssif.client.gui.controller;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXSpinner;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.RequiredFieldValidator;

import fr.elyssif.client.Config;
import fr.elyssif.client.gui.controller.SnackbarController.SnackbarMessageType;
import fr.elyssif.client.gui.model.File;
import fr.elyssif.client.gui.model.ModelCallback;
import fr.elyssif.client.gui.model.User;
import fr.elyssif.client.gui.repository.FileRepository;
import fr.elyssif.client.gui.repository.UserRepository;
import fr.elyssif.client.gui.validation.ServerValidator;
import fr.elyssif.client.gui.validation.StringMaxLengthValidator;
import fr.elyssif.client.gui.validation.StringMinLengthValidator;
import fr.elyssif.client.gui.view.ImageSlideTransition;
import fr.elyssif.client.gui.view.LookupModal;
import fr.elyssif.client.gui.view.TadaAnimation;
import fr.elyssif.client.gui.view.UserListFactory;
import fr.elyssif.client.gui.view.ViewUtils;
import fr.elyssif.client.http.FailCallback;
import fr.elyssif.client.http.FormCallback;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.util.Duration;

/**
 * Controller for the "send file" view
 * @author Jérémy LAMBERT
 *
 */
public final class SendController extends FadeController implements Lockable, Validatable {

	@FXML private ImageView image;
	@FXML private JFXSpinner spinner;

	@FXML private JFXTextField nameInput;
	@FXML private JFXTextField fileInput;
	@FXML private JFXTextField recipientInput;

	@FXML private JFXButton browseButton;
	@FXML private JFXButton recipientButton;
	@FXML private JFXButton encryptButton;

	@FXML private VBox formContainer;

	private java.io.File selectedFile;
	private User selectedUser;
	private FileRepository fileRepository;

	private HashMap<String, ServerValidator> serverValidators;
	private SimpleBooleanProperty disableProperty;

	private double progress = 0;

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
		if(file != null && file.isFile()) {
			fileInput.setText(file.getName());
			selectedFile = file;
			fileInput.validate();
		}
	}

	@FXML
	public void recipientClicked() {
		LookupModal<User> modal = new LookupModal<User>(new UserRepository());
		modal.setTitle("recipient");
		modal.setHeader("lookup-recipient");
		modal.setTableFactory(new UserListFactory());

		User user = modal.showDialog(getPane().getScene().getWindow());
		if(user != null) {
			selectedUser = user;
			recipientInput.setText(selectedUser.getName().get());
			recipientInput.validate();
		}
	}

	@FXML
	public void encryptClicked() {
		if(validateAll()) {
			setLocked(true);

			File fileModel = new File();
			fileModel.setName(nameInput.getText());
			fileModel.setRecipientId(selectedUser.getId().get());
			fileModel.setHash("b94d27b9934d3e08a52e52d7da7dabfac484efe37a5380ee9088f7ace2efcde9"); // TODO hash
			fileRepository.store(fileModel, new ModelCallback<File>() {

				public void run() {
					Logger.getGlobal().info(fileModel.getId().asString().get());
					Logger.getGlobal().info(fileModel.getPublicKey().get());
					resetForm();

					playEncryptTransition();
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

	private void playEncryptTransition() {
		spinner.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);
		FadeTransition ft = ViewUtils.createFadeOutTransition(formContainer, Duration.millis(750));
		FadeTransition ft2 = ViewUtils.createFadeOutTransition(encryptButton, Duration.millis(750));
		FadeTransition ft3 = ViewUtils.createFadeInTransition(spinner, Duration.millis(750));
		ft3.setDelay(Duration.millis(1000));

		ImageSlideTransition slide = new ImageSlideTransition(image, getFadePane().getHeight(), Duration.millis(750));
		spinner.toFront();
		ft.play();
		ft2.play();
		ft3.play();
		slide.play();

		slide.setOnFinished(e -> {
			resetForm();
			Thread t = new Thread(() -> {
				// TODO encrypt
				Random random = new Random();
				while(progress < 1) {
					progress += random.nextDouble() / 100;

					Platform.runLater(() -> spinner.setProgress(progress));

					try {
						Thread.sleep(25);
					} catch (InterruptedException ie) {
						ie.printStackTrace();
					}
				}

				Platform.runLater(() -> {
					TadaAnimation tada = new TadaAnimation(image);
					tada.play();
					tada.setOnFinished(e2 -> {

						saveFile();

						ft.setRate(-1);
						ft2.setRate(-1);
						ft3.setRate(-1);
						slide.revert();

						ft3.setDelay(Duration.millis(0));

						ft.setDelay(Duration.millis(1000));
						ft2.setDelay(Duration.millis(1000));
						slide.setDelay(Duration.millis(1000));

						ft.play();
						ft2.play();
						ft3.play();
						slide.play();
						slide.setOnFinished(e3 -> {
							spinner.toBack();
							progress = 0;
							setLocked(false);
						});
					});
				});
			});
			t.start();
		});
	}

	private void saveFile() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle(getBundle().getString("save"));
		java.io.File file = fileChooser.showSaveDialog(getPane().getScene().getWindow());
		if(file != null) {
			// TODO copy temp file to chosen destination
		} else {
			// TODO file = tempfile
		}

		SnackbarController.getInstance().message(getBundle().getString("encrypt-success").replace("\\n", "\n"), SnackbarMessageType.SUCCESS, 10000);
		Desktop desktop = Desktop.getDesktop();
		try {
			desktop.open(file.getParentFile());
		} catch (IOException e) {
			Logger.getGlobal().log(Level.SEVERE, "Couldn't open file explorer.", e);
		}
	}

	@Override
	public void setLocked(boolean locked) {
		disableProperty.set(locked);
		((Lockable) MainController.getInstance().getController("app")).setLocked(locked);
	}

	@Override
	public void bindControls() {
		formContainer.disableProperty().bind(disableProperty);
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
