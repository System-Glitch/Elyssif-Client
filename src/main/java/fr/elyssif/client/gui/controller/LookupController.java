package fr.elyssif.client.gui.controller;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTextField;

import fr.elyssif.client.Config;
import fr.elyssif.client.gui.controller.SnackbarController.SnackbarMessageType;
import fr.elyssif.client.gui.model.Model;
import fr.elyssif.client.gui.model.User;
import fr.elyssif.client.gui.repository.Repository;
import fr.elyssif.client.gui.view.ListFactory;
import fr.elyssif.client.http.FailCallback;
import fr.elyssif.client.http.PaginateCallback;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;

/**
 * Controller for the custom lookup modal.
 * @author Jérémy LAMBERT
 *
 */
public final class LookupController extends Controller {

	private Repository<? extends Model<?>> repository;

	@FXML private Label title;
	@FXML private JFXTextField input;
	@FXML private JFXListView<?> results;

	private JFXDialog parentDialog;
	private ObservableList<Model<?>> list;
	private Model<?> selected;
	private Runnable callback;

	public void initialize(URL location, ResourceBundle resources) {
		if(Config.getInstance().isVerbose())
			Logger.getGlobal().info("Loading lookup controller.");
		super.initialize(location, resources);

		list = FXCollections.observableArrayList();
		show(false);
	}

	public void focusInput() {
		Platform.runLater(() -> input.requestFocus());
	}

	/**
	 * Get the repository used for the lookup.
	 * @return the repository used for the lookup
	 */
	public final Repository<? extends Model<?>> getRepository() {
		return repository;
	}

	/**
	 * Set the repository used for the lookup.
	 * The method <code>getWhere()</code> is used for the lookup.
	 * @param repository
	 */
	public final void setRepository(Repository<? extends Model<?>> repository) {
		this.repository = repository;
	}

	/**
	 * Set the heading content.
	 * The given title should be a resource string.
	 * @param title
	 */
	public final void setHeader(String title) {
		this.title.setText(getBundle().getString(title));
	}

	/**
	 * Get the selected resource
	 * @return the selected resource
	 */
	public final Model<?> getSelected() {
		return selected;
	}

	public final void setParentDialog(JFXDialog parentDialog) {
		this.parentDialog = parentDialog;
	}

	/**
	 * Set the callback executed when the dialog closes.
	 * @param callback
	 */
	public final void setCallback(Runnable callback) {
		this.callback = callback;
	}

	@FXML
	private void search() {

		String search = input.getText().trim();

		if(search.isEmpty()) {
			list.clear();
		} else {
			repository.getWhere(search, new PaginateCallback<User>() {

				public void run() {
					list.clear();

					if(!input.getText().trim().isEmpty()) {
						for(Model<?> model : getPaginator().getItems()) {
							list.add(model);
						}
					}
				}
			}, new FailCallback() {

				public void run() {
					String message = getFullMessage();
					if(message.startsWith("%")) {
						message = getBundle().getString(message.substring(1));
					}
					SnackbarController.getInstance().message(message, SnackbarMessageType.ERROR);
				}

			});			
		}

	}

	@FXML
	private void cancel() {
		selected = null;
		if(callback != null) {
			callback.run();
		}
		parentDialog.close();
	}

	@FXML
	private void select() {
		selected = (Model<?>) results.getSelectionModel().getSelectedItem();
		if(callback != null) {
			callback.run();
		}
		parentDialog.close();
	}

	public void initList(ListFactory<? extends Model<?>> factory) {
		factory.make(results, list, event -> {
			@SuppressWarnings("unchecked")
			ListCell<Model<?>> cell = (ListCell<Model<?>>) event.getTarget();
			if (event.getClickCount() == 2 && !cell.isEmpty()) {
				selected = cell.getItem();
				if(callback != null) {
					callback.run();
				}
				parentDialog.close();
			}
		});
	}

}
