package fr.elyssif.client.gui.view;

import java.io.IOException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.jfoenix.controls.JFXDecorator;

import fr.elyssif.client.gui.controller.LookupController;
import fr.elyssif.client.gui.controller.MainController;
import fr.elyssif.client.gui.model.Model;
import fr.elyssif.client.gui.repository.Repository;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

/**
 * Modal for generic lookup through REST API.
 * @author Jérémy LAMBERT
 *
 */
public class LookupModal<T extends Model<T>> {

	private static final int DEFAULT_WIDTH = 400;
	private static final int DEFAULT_HEIGHT = 580;

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
	 * @param title the title of the modal
	 * @param ownerWindow the owner window of the displayed dialog
	 * @return the selected resource id or -1 if no resource has been selected
	 */
	@SuppressWarnings("unchecked")
	public T showDialog(Window ownerWindow) {
		Stage dialog = new Stage();

		dialog.setTitle(title);
		dialog.initOwner(ownerWindow);
		dialog.initModality(Modality.APPLICATION_MODAL);
		dialog.setMinHeight(DEFAULT_HEIGHT);
		dialog.setMinWidth(DEFAULT_WIDTH);
		dialog.setHeight(DEFAULT_HEIGHT);
		dialog.setWidth(DEFAULT_WIDTH);

		try {
			ResourceBundle bundle = MainController.getInstance().getBundle();
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/LookupView.fxml"));
			loader.setResources(bundle);

			VBox rootLayout = (VBox) loader.load();
			controller = (LookupController) loader.getController();
			controller.setRepository(repository);
			controller.setHeader(header);
			controller.initList(factory);

			JFXDecorator decorator = new JFXDecorator(dialog, rootLayout, false, false, false);
			Scene scene = new Scene(decorator);
			scene.getStylesheets().addAll(ownerWindow.getScene().getStylesheets());
			ViewUtils.disableContextMenu(scene);

			dialog.setResizable(false);
			dialog.setScene(scene);
			dialog.setTitle(bundle.getString(title));
			dialog.showAndWait();

			return (T) controller.getSelected();
		} catch (IOException e) {
			Logger.getGlobal().log(Level.SEVERE, "Couldn't load lookup dialog.", e);
			return null;
		}
	}

}
