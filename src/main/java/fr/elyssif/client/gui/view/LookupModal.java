package fr.elyssif.client.gui.view;

import java.io.IOException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.jfoenix.controls.JFXDialog;

import fr.elyssif.client.gui.controller.LookupController;
import fr.elyssif.client.gui.controller.MainController;
import fr.elyssif.client.gui.model.Model;
import fr.elyssif.client.gui.model.ModelCallback;
import fr.elyssif.client.gui.repository.Repository;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/**
 * Modal for generic lookup through REST API.
 * @author Jérémy LAMBERT
 *
 */
public class LookupModal<T extends Model<T>> {

	private static final int DEFAULT_WIDTH = 400;
	private static final int DEFAULT_HEIGHT = 500;

	private Repository<? extends Model<?>> repository;
	private ListFactory<T> factory;
	private LookupController controller;
	private String title = "lookup";
	private String header = "lookup-header";

	public LookupModal(Repository<T> repository) {
		this.repository = repository;
	}

	/**
	 * Get the title of the modal.
	 * @return the title of the modal
	 */
	public final String getTitle() {
		return title;
	}

	/**
	 * Set the title of the modal.
	 * @param title the title of the modal
	 */
	public final void setTitle(String title) {
		this.title = title;
	}

	/**
	 * Get the header of the modal.
	 * @return the header of the modal
	 */
	public final String getHeader() {
		return title;
	}

	/**
	 * Set the header of the modal.
	 * @param header the header of the modal
	 */
	public final void setHeader(String header) {
		this.header = header;
	}

	public void setTableFactory(ListFactory<T> tableFactory) {
		factory = tableFactory;
	}

	/**
	 * Show a new lookup dialog. The method doesn't return until the displayed dialog is dismissed.
	 * If the owner window for the dialog is set, input to all windows in the dialog's owner
	 * chain is blocked while the dialog is being shown.
	 * @param ownerPane the container pane on which the dialog will popup
	 * @param callback the callback executed when the dialog closes. Contains a reference to
	 * the selected record, accessible via <code>getModel()</code>
	 */
	@SuppressWarnings("unchecked")
	public void showDialog(StackPane ownerPane, ModelCallback<T> callback) {

		try {
			ResourceBundle bundle = MainController.getInstance().getBundle();
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/LookupView.fxml"));
			loader.setResources(bundle);

			VBox rootLayout = (VBox) loader.load();
			rootLayout.setPrefWidth(DEFAULT_WIDTH);
			rootLayout.setPrefHeight(DEFAULT_HEIGHT);
			final JFXDialog dialog = new JFXDialog(ownerPane, rootLayout, JFXDialog.DialogTransition.CENTER);

			controller = (LookupController) loader.getController();
			controller.setRepository(repository);
			controller.setHeader(header);
			controller.initList(factory);
			controller.setParentDialog(dialog);
			controller.setCallback(() -> {
				callback.setModel((T) controller.getSelected());
				callback.run();
			});

			dialog.setOnDialogOpened(e -> controller.focusInput());
			dialog.show();

		} catch (IOException e) {
			Logger.getGlobal().log(Level.SEVERE, "Couldn't load lookup dialog.", e);
		}
	}

}
