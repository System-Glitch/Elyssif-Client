package fr.elyssif.client.gui.controller;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import com.jfoenix.controls.JFXListView;

import fr.elyssif.client.Config;
import fr.elyssif.client.callback.FailCallback;
import fr.elyssif.client.callback.PaginateCallback;
import fr.elyssif.client.gui.controller.SnackbarController.SnackbarMessageType;
import fr.elyssif.client.gui.model.File;
import fr.elyssif.client.gui.model.Model;
import fr.elyssif.client.gui.repository.FileRepository;
import fr.elyssif.client.gui.view.FileListFactory;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;

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
	private FailCallback failCallback;

	private ObservableList<File> sentList;
	private ObservableList<File> receivedList;

	public void initialize(URL location, ResourceBundle resources) {
		if(Config.getInstance().isVerbose())
			Logger.getGlobal().info("Loading home controller.");
		super.initialize(location, resources);

		show(false);

		sentList = FXCollections.observableArrayList();
		receivedList = FXCollections.observableArrayList();

		failCallback = new FailCallback() {
			public void run() {
				SnackbarController.getInstance().message(getBundle().getString(getFullMessage()), SnackbarMessageType.ERROR);
			}
		};

		var factory = new FileListFactory();
		factory.setMode(FileListFactory.MODE_SEND);
		factory.make(sentListView, sentList); // TODO handle on click

		factory = new FileListFactory();
		factory.setMode(FileListFactory.MODE_RECEIVE);
		factory.make(receivedListView, receivedList); // TODO handle on click

		sentPaginateController.setOnPageChange(() -> refreshSent());
		receivedPaginateController.setOnPageChange(() -> refreshReceived());
		Platform.runLater(() -> repository = new FileRepository());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void show(boolean transition, Controller backController) {
		super.show(transition, backController);
		refresh();
	}

	protected void refresh() {
		if(repository != null) {
			refreshSent();
			refreshReceived();
		}
	}

	private void refreshSent() {
		repository.getSent(sentPaginateController.getPage(), new PaginateCallback<File>() {
			public void run() {
				sentList.clear();

				for(Model<?> model : getPaginator().getItems()) {
					sentList.add((File) model);
				}

				sentListView.scrollTo(0);
				sentPaginateController.setPaginator(getPaginator());
			}
		}, failCallback);
	}

	private void refreshReceived() {
		repository.getReceived(receivedPaginateController.getPage(), new PaginateCallback<File>() {
			public void run() {
				receivedList.clear();

				for(Model<?> model : getPaginator().getItems()) {
					receivedList.add((File) model);
				}

				receivedListView.scrollTo(0);
				receivedPaginateController.setPaginator(getPaginator());
			}
		}, failCallback);
	}

}
