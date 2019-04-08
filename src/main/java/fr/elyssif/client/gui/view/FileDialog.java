package fr.elyssif.client.gui.view;

import java.io.IOException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.jfoenix.controls.JFXDialog;

import fr.elyssif.client.gui.controller.FileDialogController;
import fr.elyssif.client.gui.model.File;
import fr.elyssif.client.gui.repository.FileRepository;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/**
 * File details dialog.
 * @author Jérémy LAMBERT
 *
 */
public class FileDialog {

	private FileRepository repository;
	private FileDialogController controller;
	private ResourceBundle bundle;

	private File file;

	/**
	 * Create a new instance of a lookup modal
	 * @param file the file to display
	 * @param repository the repository used for lookup
	 * @param bundle the language bundle to use for this dialog
	 */
	public FileDialog(File file, FileRepository repository, ResourceBundle bundle) {
		this.file = file;
		this.repository = repository;
		this.bundle = bundle;
	}

	/**
	 * Get the file displayed by this modal.
	 * @return the file displayed by this modal
	 */
	public final File getFile() {
		return file;
	}

	/**
	 * Set the file displayed by this modal.
	 * @param file the file displayed by this modal
	 */
	public final void setFile(File file) {
		this.file = file;
	}

	/**
	 * Show a new file dialog.
	 * If the owner window for the dialog is set, input to all windows in the dialog's owner
	 * chain is blocked while the dialog is being shown. Clicking the overlay closes the dialog.
	 * @param ownerPane the container pane on which the dialog will popup
	 */
	public void showDialog(StackPane ownerPane) {

		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/FileDialogView.fxml"));
			loader.setResources(bundle);

			VBox rootLayout = (VBox) loader.load();
			final JFXDialog dialog = new JFXDialog(ownerPane, rootLayout, JFXDialog.DialogTransition.CENTER);

			controller = (FileDialogController) loader.getController();
			controller.setRepository(repository);
			controller.setParentDialog(dialog);
			controller.setFile(file);

			dialog.show();

		} catch (IOException e) {
			Logger.getGlobal().log(Level.SEVERE, "Couldn't load file dialog.", e);
		}
	}

}
