package fr.elyssif.client.gui.controller;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import com.jfoenix.controls.JFXListView;

import fr.elyssif.client.Config;
import fr.elyssif.client.gui.model.File;
import fr.elyssif.client.gui.view.FileListFactory;
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

	private ObservableList<File> sentList;
	private ObservableList<File> receivedList;

	public void initialize(URL location, ResourceBundle resources) {
		if(Config.getInstance().isVerbose())
			Logger.getGlobal().info("Loading home controller.");
		super.initialize(location, resources);

		show(false);

		sentList = FXCollections.observableArrayList();
		receivedList = FXCollections.observableArrayList();

		new FileListFactory().make(sentListView, sentList); // TODO handle on click
		new FileListFactory().make(receivedListView, receivedList); // TODO handle on click
	}

}
