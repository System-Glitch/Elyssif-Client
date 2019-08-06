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
import java.util.logging.Logger;

import com.jfoenix.controls.JFXListView;

import fr.elyssif.client.Config;
import fr.elyssif.client.callback.FailCallbackData;
import fr.elyssif.client.callback.PaginateCallbackData;
import fr.elyssif.client.callback.RestCallback;
import fr.elyssif.client.gui.controller.SnackbarController.SnackbarMessageType;
import fr.elyssif.client.gui.model.File;
import fr.elyssif.client.gui.model.Model;
import fr.elyssif.client.gui.repository.FileRepository;
import fr.elyssif.client.gui.view.FileDialog;
import fr.elyssif.client.gui.view.FileListFactory;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.layout.StackPane;

/**
 * Controller for the "home" view
 * @author Jérémy LAMBERT
 *
 */
public final class HomeController extends FadeController {

	@FXML private JFXListView<File> sentListView;
	@FXML private JFXListView<File> receivedListView;

	@FXML private PaginateController sentPaginateController;
	@FXML private PaginateController receivedPaginateController;

	private FileRepository repository;
	private RestCallback failCallback;

	private FileDialog dialog;

	private ObservableList<File> sentList;
	private ObservableList<File> receivedList;

	public void initialize(URL location, ResourceBundle resources) {
		if(Config.getInstance().isVerbose())
			Logger.getGlobal().info("Loading home controller.");
		super.initialize(location, resources);

		sentList = FXCollections.observableArrayList();
		receivedList = FXCollections.observableArrayList();
		show(false);

		failCallback = data -> SnackbarController.getInstance().message(getBundle().getString(((FailCallbackData) data).getFullMessage()), SnackbarMessageType.ERROR);
		initLists();

		sentPaginateController.setOnPageChange(() -> refreshSent());
		receivedPaginateController.setOnPageChange(() -> refreshReceived());
		Platform.runLater(() -> repository = new FileRepository());
	}

	private void initLists() {
		var factory = new FileListFactory(getBundle());
		factory.setMode(FileListFactory.MODE_SEND);
		factory.make(sentListView, sentList, event -> {
			File file = sentListView.getSelectionModel().getSelectedItem();
			if (event.getClickCount() == 2 && file != null) {
				dialog = new FileDialog(file, repository, getBundle());
				dialog.showDialog((StackPane) MainController.getInstance().getPane(), FileDialog.MODE_SEND);
			}
		});

		factory = new FileListFactory(getBundle());
		factory.setMode(FileListFactory.MODE_RECEIVE);
		factory.make(receivedListView, receivedList, event -> {
			File file = receivedListView.getSelectionModel().getSelectedItem();
			if (event.getClickCount() == 2 && file != null) {
				dialog = new FileDialog(file, repository, getBundle());
				dialog.showDialog((StackPane) MainController.getInstance().getPane(), FileDialog.MODE_RECEIVE);
			}
		});
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void show(boolean transition, Controller backController) {
		super.show(transition, backController);
		sentList.clear();
		receivedList.clear();
		refresh();
	}

	@Override
	protected void onNext() {
		super.onNext();
		if(dialog != null) {
			dialog.closeDialog();
		}
	}

	/**
	 * Refresh the sent and received file lists.
	 */
	public void refresh() {
		if(repository != null && getPane().isManaged()) {
			refreshSent();
			refreshReceived();
		}
	}

	private void refreshSent() {
		repository.getSent(sentPaginateController.getPage(), data -> {
			@SuppressWarnings("unchecked")
			var paginator = ((PaginateCallbackData<File>) data).getPaginator();
			sentList.clear();

			for(Model<?> model : paginator.getItems()) {
				sentList.add((File) model);
			}

			sentListView.scrollTo(0);
			sentPaginateController.setPaginator(paginator);
		}, failCallback);
	}

	private void refreshReceived() {
		repository.getReceived(receivedPaginateController.getPage(), data -> {
			@SuppressWarnings("unchecked")
			var paginator = ((PaginateCallbackData<File>) data).getPaginator();
			receivedList.clear();

			for(Model<?> model : paginator.getItems()) {
				receivedList.add((File) model);
			}

			receivedListView.scrollTo(0);
			receivedPaginateController.setPaginator(paginator);
		}, failCallback);
	}

	/**
	 * Remove a file from the file lists. The file is not
	 * deleted in the database.
	 * @param file
	 */
	public void removeFile(File file) {
		removeFromList(sentList, file);
		removeFromList(receivedList, file);
	}

	private boolean removeFromList(ObservableList<File> list, File file) {
		for(File f : list) {
			if(f.getId().get() == file.getId().get()) {
				return list.remove(f);
			}
		}
		return false;
	}

}
