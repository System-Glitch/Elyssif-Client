package fr.elyssif.client.gui.controller;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import com.jfoenix.controls.JFXDialog;

import fr.elyssif.client.Config;
import fr.elyssif.client.gui.model.File;
import fr.elyssif.client.gui.repository.FileRepository;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

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
	@FXML private Label sentLabel;
	@FXML private Label receivedLabel;

	private JFXDialog parentDialog;
	private File file;

	public void initialize(URL location, ResourceBundle resources) {
		if(Config.getInstance().isVerbose())
			Logger.getGlobal().info("Loading file dialog controller.");
		super.initialize(location, resources);

		show(false);
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
	 */
	public final void setFile(File file) {
		this.file = file;
		// TODO set controls values
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
	private void saveClicked() {
		
	}
	
	@FXML
	private void deleteClicked() {
		
	}

}
