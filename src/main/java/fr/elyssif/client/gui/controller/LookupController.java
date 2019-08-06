/*
 * Elyssif-Client
 * Copyright (C) 2019 Jérémy LAMBERT (System-Glitch)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
 package fr.elyssif.client.gui.controller;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Logger;

import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTextField;

import fr.elyssif.client.Config;
import fr.elyssif.client.callback.FailCallbackData;
import fr.elyssif.client.callback.PaginateCallbackData;
import fr.elyssif.client.gui.controller.SnackbarController.SnackbarMessageType;
import fr.elyssif.client.gui.model.Model;
import fr.elyssif.client.gui.repository.Repository;
import fr.elyssif.client.gui.view.ListFactory;
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

	private static final long SEARCH_DELAY = 1000;

	private Repository<? extends Model<?>> repository;

	@FXML private Label title;
	@FXML private JFXTextField input;
	@FXML private JFXListView<?> results;

	private JFXDialog parentDialog;
	private ObservableList<Model<?>> list;
	private Model<?> selected;
	private Runnable callback;

	private Timer searchTimer;

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

		if(search.length() > 255) {
			search = search.substring(0, 255);
			input.setText(search);
			input.positionCaret(254);
		}

		if(search.length() < 3) {
			list.clear();
		} else {
			final String effectiveSearch = search;
			// Delay search request to avoid spamming
			if(searchTimer != null) {
				searchTimer.cancel();
			}

			searchTimer = new Timer(true);
			searchTimer.schedule(new TimerTask() {

				@Override
				public void run() {
					repository.getWhere(effectiveSearch, data -> {
						list.clear();

						if(!input.getText().trim().isEmpty()) {
							for(Model<?> model : ((PaginateCallbackData<?>) data).getPaginator().getItems()) {
								list.add(model);
							}
						}

						searchTimer.cancel();
					}, errorData -> SnackbarController.getInstance().message(getBundle().getString(((FailCallbackData) errorData).getFullMessage()), SnackbarMessageType.ERROR));
				}

			}, SEARCH_DELAY);
		}

	}

	@FXML
	public void cancel() {
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
