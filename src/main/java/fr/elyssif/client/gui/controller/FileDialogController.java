package fr.elyssif.client.gui.controller;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;

import fr.elyssif.client.Config;
import fr.elyssif.client.gui.controller.SnackbarController.SnackbarMessageType;
import fr.elyssif.client.gui.model.File;
import fr.elyssif.client.gui.model.User;
import fr.elyssif.client.gui.repository.FileRepository;
import fr.elyssif.client.gui.view.BitcoinFormatter;
import fr.elyssif.client.gui.view.FileDialog;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

/**
 * Controller for the file dialog.
 * @author Jérémy LAMBERT
 *
 */
public final class FileDialogController extends Controller {

	private FileRepository repository;

	@FXML private Label title;
	@FXML private Label fromLabel;
	@FXML private Label toLabel;
	@FXML private Label priceLabel;
	@FXML private Label sentLabel;
	@FXML private Label receivedLabel;

	@FXML private JFXButton decryptButton;
	@FXML private JFXButton deleteButton;

	private JFXDialog confirmDialog;
	private JFXDialog parentDialog;
	private File file;

	public void initialize(URL location, ResourceBundle resources) {
		if(Config.getInstance().isVerbose())
			Logger.getGlobal().info("Loading file dialog controller.");
		super.initialize(location, resources);

		show(false);

		decryptButton.managedProperty().bind(decryptButton.visibleProperty());
		deleteButton.managedProperty().bind(deleteButton.visibleProperty());
	}

	/**
	 * Get the repository used for the file actions such as delete.
	 * @return the repository used
	 */
	public final FileRepository getRepository() {
		return repository;
	}

	/**
	 * Get the file displayed in the view associated with this controller.
	 * @return the file displayed in the view associated with this controller
	 */
	public final File getFile() {
		return file;
	}

	/**
	 * Set the file displayed in the view associated with this controller.
	 * @param file the file displayed in the view associated with this controller
	 * @param mode specifies the fields visible on the dialog.
	 * If MODE_SENT, the "decrypt" button is hidden.
	 * If MODE_RECEIVE, the "delete" button is hidden.
	 */
	public final void setFile(File file, int mode) {
		this.file = file;

		ResourceBundle bundle = getBundle();

		title.setText(file.getName().get());

		User sender = mode == FileDialog.MODE_RECEIVE ? file.getSender().get() : MainController.getInstance().getAuthenticator().getUser();
		fromLabel.setText(sender.getName().get() + " (" + sender.getEmail().get() + ")");
		User recipient = mode == FileDialog.MODE_SEND ? file.getRecipient().get() : MainController.getInstance().getAuthenticator().getUser();
		toLabel.setText(recipient.getName().get() + " (" + recipient.getEmail().get() + ")");

		double price = file.getPrice().get();
		priceLabel.setText(price > 0 ? new BitcoinFormatter(price).format() : bundle.getString("free"));

		Date sentDate = file.getCipheredAt().get();
		Date receivedDate = file.getDecipheredAt().get();
		SimpleDateFormat format = new SimpleDateFormat(bundle.getString("date-format"));

		sentLabel.setText(sentDate != null ? format.format(sentDate) : bundle.getString("pending"));
		receivedLabel.setText(receivedDate != null ? format.format(receivedDate) : bundle.getString("pending"));

		decryptButton.setVisible(mode == FileDialog.MODE_RECEIVE);
		deleteButton.setVisible(mode == FileDialog.MODE_SEND);
	}

	/**
	 * Set the repository used for the file actions such as delete.
	 * @param repository
	 */
	public final void setRepository(FileRepository repository) {
		this.repository = repository;
	}

	public final void setParentDialog(JFXDialog parentDialog) {
		this.parentDialog = parentDialog;
	}

	@FXML
	private void decryptClicked() {
		parentDialog.close();
		Controller appController = MainController.getInstance().getController("app");
		Controller receiveViewController = appController.getController("container").getController("receive");
		SideMenuController sideMenuController = (SideMenuController) appController.getController("sideMenu");
		sideMenuController.getCurrentController().showNext(receiveViewController, true);
		sideMenuController.setCurrentController(receiveViewController);
	}

	/**
	 * Close the dialog.
	 */
	public void close() {
		if(confirmDialog != null) {
			confirmDialog.close();
		}

		if(parentDialog != null) {
			parentDialog.close();
		}
	}

	@FXML
	private void deleteClicked() {
		openConfirmDialog();
	}

	private void openConfirmDialog() {
		parentDialog.setOverlayClose(false);

		confirmDialog = new JFXDialog();
		confirmDialog.setDialogContainer((StackPane) getPane().getParent());

		JFXDialogLayout content = new JFXDialogLayout();
		Label header = new Label(getBundle().getString("confirm"), new ImageView("view/img/warning.png"));
		header.getStyleClass().add("text-white");
		content.setHeading(header);
		Label body = new Label(getBundle().getString("file-delete-notice").replace("\\n", "\n"));
		body.getStyleClass().add("text-md");
		content.setBody(body);
		content.getStyleClass().add("dialog-warning");

		JFXButton cancelButton = new JFXButton(getBundle().getString("cancel"));
		cancelButton.setMaxHeight(Double.MAX_VALUE);
		cancelButton.setOnAction(e -> {
			confirmDialog.close();
		});

		JFXButton acceptButton = new JFXButton(getBundle().getString("yes"));
		acceptButton.getStyleClass().add("red-A700");
		acceptButton.setOnAction(e -> {
			deleteFile(confirmDialog, acceptButton, cancelButton);
		});
		ImageView image = new ImageView("view/img/delete.png");
		image.setFitWidth(24);
		image.setFitHeight(24);
		acceptButton.setGraphic(image);


		content.setActions(cancelButton, acceptButton);

		confirmDialog.setContent(content);
		confirmDialog.setTransitionType(JFXDialog.DialogTransition.CENTER);
		confirmDialog.setOverlayClose(false);

		confirmDialog.setOnDialogClosed(event -> {
			parentDialog.setOverlayClose(true);
		});

		confirmDialog.show();
	}

	private void deleteFile(JFXDialog dialog, JFXButton acceptButton, JFXButton cancelButton) {
		acceptButton.setDisable(true);
		cancelButton.setDisable(true);
		repository.destroy(file, data -> {
			if(data.getStatus() == 204) {
				dialog.close();
				parentDialog.close();
				((HomeController) MainController.getInstance().getController("app").getController("container").getController("home")).removeFile(file);
			} else if(data.getStatus() == 403) {
				SnackbarController.getInstance().message(getBundle().getString("forbidden"), SnackbarMessageType.ERROR);
			} else {
				SnackbarController.getInstance().message(getBundle().getString("error") + data.getResponse().getRawBody(), SnackbarMessageType.ERROR);
			}
			acceptButton.setDisable(false);
			cancelButton.setDisable(false);
		});
	}

}
